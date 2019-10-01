package br.edu.ifpr.tcc.atividade.imagem.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import br.edu.ifpr.tcc.R;

/**
 * Created by Enzo on 10/09/2016.
 */
public class CustomSwipeAdapter extends PagerAdapter {

    private Bitmap bmp[];
    private Context ctx;
    private LayoutInflater layoutInflater;

    public CustomSwipeAdapter (Context ctx, Bitmap bmp[]) {
        this.ctx = ctx;
        this.bmp = bmp;
    }

    @Override
    public int getCount() {
        return bmp.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.swipe_layout, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewerSlider);

        imageView.setImageBitmap(bmp[position]);
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}
