package me.skywave.maternitycarelocker.VO;

import me.skywave.maternitycarelocker.R;

/**
 * Created by Jeong on 2016. 9. 17..
 */
public class WeatherVO {
    private String temperature;
    private int icon;
    private String iconDescription;

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setIcon(String iconId) {
        String id = iconId.substring(0, 2);
        switch (id) {
            case "01":
                icon = R.drawable.w_01;
                iconDescription = "맑음";
                break;
            case "02":
            case "03":
            case "04":
                icon = R.drawable.w_02;
                iconDescription = "구름";
                break;
            case "09":
            case "10":
                icon = R.drawable.w_03;
                iconDescription = "비";
                break;
            case "11":
                icon = R.drawable.w_04;
                iconDescription = "천둥";
                break;
            case "13":
                icon = R.drawable.w_05;
                iconDescription = "눈";
                break;
            case "50":
                icon = R.drawable.w_06;
                iconDescription = "안개";
                break;
        }
    }

    public String getTemperature() {
        return temperature;
    }

    public int getIcon() {
        return icon;
    }

    public String getIconDescription() {
        return iconDescription;
    }
}
