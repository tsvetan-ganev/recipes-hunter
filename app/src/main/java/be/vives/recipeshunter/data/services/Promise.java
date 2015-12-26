package be.vives.recipeshunter.data.services;

public interface Promise<T, E extends Exception> {
    void resolve(T result);
    void reject(E error);
}
