package com.qlcvht.util;

import javax.swing.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * Tiện ích xử lý đa luồng bất đồng bộ với SwingWorker (Học phần Concurrency in Swing - Lab 04)
 * Giúp giao diện không bị đơ/lag khi đọc ghi Database hoặc xuất báo cáo lớn.
 */
public class AsyncWorker {

    public static <T> void execute(Callable<T> task, Consumer<T> onSuccess, Consumer<Exception> onError) {
        SwingWorker<T, Void> worker = new SwingWorker<>() {
            @Override
            protected T doInBackground() throws Exception {
                return task.call();
            }

            @Override
            protected void done() {
                try {
                    T result = get();
                    if (onSuccess != null) {
                        onSuccess.accept(result);
                    }
                } catch (Exception ex) {
                    if (onError != null) {
                        onError.accept(ex);
                    } else {
                        ex.printStackTrace();
                    }
                }
            }
        };
        worker.execute();
    }

    public static void execute(Runnable task, Runnable onDone) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                task.run();
                return null;
            }

            @Override
            protected void done() {
                if (onDone != null) {
                    onDone.run();
                }
            }
        };
        worker.execute();
    }
}
