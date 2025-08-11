package br.jus.tremt.soberania.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import br.jus.tremt.soberania.BuildConfig;
import br.jus.tremt.soberania.R;

/**
 * Created by jorgebublitz on 09/03/2018.
 */

public class DialogSobre {
    private Context ctx;

    public DialogSobre(Context ctx) {
        this.ctx = ctx;
    }

    public void show() {
        final Dialog dialog = new Dialog(ctx);

        dialog.setContentView(R.layout.sobre);
        dialog.setTitle(ctx.getResources().getString(R.string.app_name));
        dialog.setCanceledOnTouchOutside(true);
        String formattedText1 = null;
        String formattedText2 = null;

        formattedText1 = "Aplicativo Soberano<br>" + BuildConfig.VERSION_NAME + ctx.getString(R.string.txt_info1_mt);
        formattedText2 = ctx.getString(R.string.txt_info2_mt);

        TextView txtInfo1 = (TextView) dialog
                .findViewById(R.id.txtInfo1);
        Spanned result = Html.fromHtml(formattedText1);
        txtInfo1.setText(result);

        TextView txtInfo2 = (TextView) dialog
                .findViewById(R.id.txtInfo2);
        result = Html.fromHtml(formattedText2);
        txtInfo2.setText(result);

        ImageButton btnOK = (ImageButton) dialog
                .findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ImageView imgApp = (ImageView) dialog.findViewById(R.id.imgApp);
        imgApp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Spanned result = Html.fromHtml(ctx.getString(R.string.txt_info3_mt));
                TextView txtInfo2 = (TextView) dialog
                        .findViewById(R.id.txtInfo2);
                txtInfo2.setText(result);
                return false;
            }
        });
        dialog.show();
    }
}
