package br.jus.tre_mt.caixa1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import dao.Midia;

import static android.media.ThumbnailUtils.createVideoThumbnail;

/**
 * Created by jorgebublitz on 16/08/16.
 */
public class MidiaAdapter extends BaseAdapter {

    private Context ctx;
    private List<Midia> midias;
    private int id;

    public MidiaAdapter(Context ctx, List<Midia> midias) {
        this.ctx = ctx;
        this.midias = midias;
    }

    @Override
    public int getCount() {
        return midias.size();
    }

    @Override
    public Object getItem(int i) {
        return midias.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String nome = midias.get(i).getFilename();
        View v = LayoutInflater.from(ctx).inflate(R.layout.foto_row, null);
        TextView txt = (TextView) v.findViewById(R.id.grid_item_title);
        txt.setText("Midia " + Integer.toString(i + 1));
        //txt.setText(nome); // para debugar

        ImageView img = (ImageView) v.findViewById(R.id.grid_item_image);
        String ext, ftipo;
        Bitmap bitmap = null;
        File fmidia = new File(nome);
        if (fmidia.exists()) {
            ext = nome.substring(nome.lastIndexOf(".") + 1);
            ftipo = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
            try {
                if (ftipo.contains("video")) {
                    bitmap = createVideoThumbnail(nome,
                            MediaStore.Video.Thumbnails.MINI_KIND);
                } else if (ftipo.contains("image")) {
                    bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(nome),
                            200, 200, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                } else {
                    bitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ico_mic);
                }
                img.setImageBitmap(bitmap);
            } catch (Exception e) {
            }
        }
        return v;
    }
}
