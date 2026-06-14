public class StreamTransformer {
    public void transformStream(byte[] in, byte[] out, int len, boolean encrypt, boolean compress, int key, int lvl, int block, int pad) {
        int i = 0;
        int j = 0;
        byte c;
        while (i < len)
            if ((c = in[i++]) != 0)
                if (encrypt)
                    if (compress)
                        out[j++] = (byte)(c ^ key + lvl * block - pad / 2);
                    else
                        out[j++] = (byte)(c ^ key);
                else
                    out[j++] = c;
    }
}
