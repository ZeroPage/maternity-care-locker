package me.skywave.maternitycarelocker.utils;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import me.skywave.maternitycarelocker.locker.model.BabyfairVO;
import me.skywave.maternitycarelocker.locker.model.TimerSetVO;

public class FirebaseHelper {
    private static FirebaseUser CURRENT_USER;

    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public interface RequestUserEventListener {
        void onEvent(FirebaseUser user);
    }

    public static void requestCurrentUser(final RequestUserEventListener listener) {
        if (CURRENT_USER == null) {
            CURRENT_USER = FirebaseAuth.getInstance().getCurrentUser();

            if (CURRENT_USER == null) {
                final Task<AuthResult> task = FirebaseAuth.getInstance().signInAnonymously();

                task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        CURRENT_USER = authResult.getUser();

                        FirebaseDatabase instance = FirebaseDatabase.getInstance();
                        instance.getReference("user/" + CURRENT_USER.getUid() + "/signup_time").setValue(ServerValue.TIMESTAMP);
                        instance.getReference("timer/" + CURRENT_USER.getUid()).setValue(false);

                        listener.onEvent(CURRENT_USER);
                    }
                });

                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CURRENT_USER = null;
                        listener.onEvent(null);
                    }
                });
            } else {
                listener.onEvent(CURRENT_USER);
            }
        } else {
            listener.onEvent(CURRENT_USER);
        }
    }

    public static void registerToken(final String token) {
        requestCurrentUser(new RequestUserEventListener() {
            @Override
            public void onEvent(FirebaseUser user) {
                if (user != null) {
                    FirebaseDatabase.getInstance().getReference("user/" + user.getUid() + "/token").setValue(token);
                }
            }
        });
    }

    public static void addTimerSet(final TimerSetVO timerSetVO) {
        requestCurrentUser(new RequestUserEventListener() {
            @Override
            public void onEvent(FirebaseUser user) {
                if (user != null) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("timer/" + user.getUid());
                    reference.push().setValue(timerSetVO);
                }
            }
        });
    }

    public interface RequestBabyfairListEventListener {
        void onEvent(List<BabyfairVO> babyfairs);
    }
    public static void requestBabyfairList(final RequestBabyfairListEventListener listener) {
        requestCurrentUser(new RequestUserEventListener() {
            @Override
            public void onEvent(FirebaseUser user) {
                if (user == null) {
                    listener.onEvent(null);
                    return;
                }

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/babyfair/");
                reference.orderByChild("dateFrom").startAt(Calendar.getInstance().getTimeInMillis())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                LinkedList<BabyfairVO> result = new LinkedList<>();
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    result.add(child.getValue(BabyfairVO.class));
                                }

                                listener.onEvent(result);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                listener.onEvent(null);
                            }
                        });
            }
        });
    }

    public interface RequestTimerSetEventListener {
        void onEvent(List<TimerSetVO> timerSet);
    }

    public static void requestTimerSet(final String uid, final RequestTimerSetEventListener listener) {
        requestCurrentUser(new RequestUserEventListener() {
            @Override
            public void onEvent(FirebaseUser user) {
                if (user != null) {
                    FirebaseDatabase.getInstance().getReference("timer/" + uid).
                            orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            LinkedList<TimerSetVO> result = new LinkedList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                result.add(snapshot.getValue(TimerSetVO.class));
                            }

                            listener.onEvent(result);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            listener.onEvent(null);
                        }
                    });
                }
            }
        });
    }

    public interface AcceptPairingEventListener {
        void onEvent(String requesterUid);
    }

    public static void acceptPairing(final String pairingCode, final AcceptPairingEventListener listener) {
        requestCurrentUser(new RequestUserEventListener() {
            @Override
            public void onEvent(final FirebaseUser user) {
                if (user == null) {
                    listener.onEvent(null);
                    return;
                }

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference reference = database.getReference("/pairing/" + pairingCode.toUpperCase());

                reference.child("requester/uid").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            listener.onEvent(null);
                            return;
                        }

                        String requesterUid = (String) dataSnapshot.getValue();

                        TreeMap<String, Object> map = new TreeMap<>();
                        map.put("uid", user.getUid());
                        map.put("timestamp", ServerValue.TIMESTAMP);

                        reference.child("accepter").updateChildren(map);

                        database.getReference("/user/" + user.getUid() + "/pair").setValue(requesterUid);

                        listener.onEvent(requesterUid);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onEvent(null);
                    }
                });
            }
        });
    }

    public static class FirebasePairing {
        public static final int CODE_LENGTH = 5;
        public static final long TIME_LIMIT = 61000;

        private ValueEventListener listener;
        private DatabaseReference accepterReference;
        private Timer timer;
        private DatabaseReference reference;
        private String pairingCode;
        private String accepterUid = null;

        private PairingEventListener successListener = null;
        private PairingEventListener failListener = null;
        private PairingEventListener preparedListener;

        private boolean closed = false;
        private Thread thread;
        private FirebaseUser user;
        private FirebaseDatabase database;

        public FirebasePairing(PairingEventListener onPrepared, PairingEventListener onSuccess, PairingEventListener onFail) {
            preparedListener = onPrepared;
            successListener = onSuccess;
            failListener = onFail;

            requestCurrentUser(new RequestUserEventListener() {
                @Override
                public void onEvent(FirebaseUser user) {
                    if (user != null) {
                        FirebasePairing.this.user = user;
                        initializePairingCode();
                    } else {
                        close(true);
                    }
                }
            });
        }

        private void initializePairingCode() {
            Runnable initialize = new Runnable() {
                @Override
                public void run() {
                    String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                    SecureRandom rnd = new SecureRandom();
                    database = FirebaseDatabase.getInstance();

                    TreeMap<String, Object> map = new TreeMap<>();
                    map.put("uid", user.getUid());
                    map.put("timestamp", ServerValue.TIMESTAMP);

                    StringBuilder builder = new StringBuilder(CODE_LENGTH);
                    for (int i = 0; i < CODE_LENGTH; i++) {
                        builder.append(AB.charAt(rnd.nextInt(AB.length())));
                    }

                    pairingCode = builder.toString();
                    reference = database.getReference("/pairing/" + pairingCode);

                    Task<Void> task = reference.child("requester").updateChildren(map);

                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (!Thread.interrupted()) {
                                initializePairingCode();
                            }
                        }
                    });

                    task.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            initializeDatabase();
                        }
                    });
                }
            };

            thread = new Thread(initialize);
            thread.start();
        }

        private void initializeDatabase() {
            listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        return;
                    }

                    accepterUid = (String) dataSnapshot.getValue();
                    if (successListener != null) {
                        successListener.onEvent();
                    }

                    database.getReference("/user/" + user.getUid() + "/pair/").setValue(accepterUid);

                    close(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    close(true);
                }
            };

            accepterReference = reference.child("accepter/uid/");
            accepterReference.addValueEventListener(listener);

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    close(true);
                }
            }, TIME_LIMIT);

            if (preparedListener != null) {
                preparedListener.onEvent();
            }
        }

        public String getPairingCode() {
            return pairingCode;
        }

        public String getAccepterUid() {
            return accepterUid;
        }

        public void close(boolean runOnFail) {
            if (closed) {
                return;
            }

            closed = true;

            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }

            if (listener != null) {
                accepterReference.removeEventListener(listener);
            }

            if (accepterUid == null && runOnFail) {
                failListener.onEvent();
            }

            if (timer != null) {
                timer.cancel();
            }
        }

        public interface PairingEventListener {
            void onEvent();
        }
    }
}
