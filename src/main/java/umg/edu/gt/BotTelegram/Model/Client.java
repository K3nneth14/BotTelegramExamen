package umg.edu.gt.BotTelegram.Model;
//autor @Kenneth//


import java.io.Serializable;

public class Client implements Serializable {
    private long clientId;
    private String name;

    public Client(int clientId, String name) {
        this.clientId = clientId;
        this.name = name;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientId=" + clientId +
                ", name='" + name + '\'' +
                '}';
    }
}
