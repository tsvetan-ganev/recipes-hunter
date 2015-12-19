package be.vives.recipeshunter.data.services;

public interface AsyncResponse<T> {
    void resolve(T result);
}
