package me.sshcrack.sutils.tools;

import java.util.Objects;

@FunctionalInterface
public interface TwoConsumer<T, K> {
    void accept(T t, K k);

    default TwoConsumer<T, K> andThen(TwoConsumer<? super T, ? super K> after) {
        Objects.requireNonNull(after);
        return (T t, K k) -> { accept(t, k); after.accept(t, k); };
    }
}
