package model.tutor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class TutorImpl extends UnicastRemoteObject implements Tutor {

    private static final Object syncObject = new Object();
    private final Queue<UUID> requestQueue;
    private final Map<UUID, Request> requests;
    private final Map<UUID, Answer> answers;

    public TutorImpl() throws RemoteException {
        super();
        requestQueue = new LinkedBlockingQueue<>();
        requests = new HashMap<>();
        answers = new HashMap<>();
    }

    @Override
    public void sendRequest(UUID oldId, Request request) {
        synchronized (syncObject) {
            // remove an old request from the datastructures
            if (requests.containsKey(oldId)) {
                requests.remove(oldId);
                requestQueue.remove(oldId);
            }

            requestQueue.add(request.getId());
            requests.put(request.getId(), request);
        }
    }

    @Override
    public Answer receiveAnswer(UUID id) {
        Answer answer;
        synchronized (syncObject) {
            answer = answers.get(id);
            answers.remove(id);
        }

        return answer;
    }

    public Request receiveRequest() {
        Request request;
        synchronized (syncObject) {
            UUID oldestId = requestQueue.poll();
            request = requests.get(oldestId);
            requests.remove(oldestId);
        }

        return request;
    }

    public void sendAnswer(Answer answer) {
        synchronized (syncObject) {
            answers.put(answer.getId(), answer);
        }
    }
}
