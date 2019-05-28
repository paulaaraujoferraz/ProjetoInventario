package br.com.inventario.domain;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import br.com.inventario.util.Base64;

public class Image {
    private String mime;
    private Bitmap bitmap;

    private static Bitmap fixMatrix(File file, Bitmap bitmap) throws IOException {
        Matrix matrix = new Matrix();
        boolean fixed = false;
        ExifInterface exif = new ExifInterface(file.getPath()); // Classe para ler tags escritas no JPEG
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL); // Orientacao que foi salva a foto

        // Rotate bitmap
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                fixed = true;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                fixed = true;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                fixed = true;
                break;
            default:
                fixed = false;
                break;
        }

        if (!fixed) {
            return bitmap;
        }

        // Correcaoo da orientacao da foto (passa a matrix)
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        bitmap.recycle();
        bitmap = null;

        return newBitmap;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public void setMimeFromImgPath(String imgPath) {
        String[] aux = imgPath.split("\\.");
        this.mime = aux[aux.length - 1];
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getBitmapBase64() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (mime.equalsIgnoreCase("png"))
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        else
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return (Base64.encodeBytes(byteArray));
    }

    public void setResizedBitmap(File file, int width, int height) {
        try {
            bitmap = BitmapFactory.decodeFile(file.getPath());

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            float scaleX = (float) width / bitmap.getWidth();
            float scaleY = (float) height / bitmap.getHeight();

            Matrix matrix = new Matrix();
            matrix.setScale(scaleX, scaleY);
            Bitmap auxBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);

            auxBitmap = fixMatrix(file, auxBitmap);

            // Retira da memoria o arquivo de imagem com tamanho original
            bitmap.recycle();
            bitmap = auxBitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
