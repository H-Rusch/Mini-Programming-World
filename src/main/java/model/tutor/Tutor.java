package model.tutor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface Tutor extends Remote {

    void sendRequest(UUID oldId, Request request) throws RemoteException;

    Answer receiveAnswer(UUID id) throws RemoteException;

    void sendAnswer(Answer answer) throws RemoteException;

    Request receiveRequest() throws RemoteException;
}
