package vn.binhnc.banhanga.model;

import java.util.List;

public class HinhAnhSanPhamModel {
    boolean success;
    String message;
    List<HinhAnh> result;

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

    public List<HinhAnh> getResult() {
        return result;
    }

    public void setResult(List<HinhAnh> result) {
        this.result = result;
    }
}
