package vn.binhnc.manager.model.EventBus;


public class DeleteOrderEvent {
    int iddonhang;
    int index;
    int status;

    public DeleteOrderEvent(int iddonhang, int index, int status) {
        this.iddonhang = iddonhang;
        this.index = index;
        this.status = status;
    }

    public int getIddonhang() {
        return iddonhang;
    }

    public void setIddonhang(int iddonhang) {
        this.iddonhang = iddonhang;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
