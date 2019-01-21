package jp.co.homuhomu.camera2test.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class FileManager {
    public static void writeFrame(File file, byte[] data) {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            bos.write(data);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}