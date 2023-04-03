package net.ddns.encante.telegram.HR.Hue;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class HueLinkButtonPressed {

    @SerializedName("/config/linkbutton")
    Boolean configLinkbutton;
}
