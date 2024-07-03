package vn.binhnc.banhanga.model;

import java.util.List;

public class YeuThichModel {
    boolean success;
    String message;
    List<YeuThich> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<YeuThich> getResult() {
        return result;
    }

    public void setResult(List<YeuThich> result) {
        this.result = result;
    }
}
