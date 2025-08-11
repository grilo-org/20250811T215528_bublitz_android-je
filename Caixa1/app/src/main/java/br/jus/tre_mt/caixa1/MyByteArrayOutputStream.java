package br.jus.tre_mt.caixa1;

import java.io.ByteArrayOutputStream;

/**
 * Created by jorgebublitz on 02/09/16.
 */
public class MyByteArrayOutputStream extends ByteArrayOutputStream {
    @Override
    public synchronized byte[] toByteArray() {
        return buf;
    }
}
