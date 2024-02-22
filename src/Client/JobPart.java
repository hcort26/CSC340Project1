package Client;

import java.io.Serializable;

public class JobPart implements Serializable {
    private String textPart;
    private int partId;

    public JobPart(String textPart, int partId) {
        this.textPart = textPart;
        this.partId = partId;
    }

    public String getTextPart() {
        return textPart;
    }

    public int getPartId() {
        return partId;
    }
}
