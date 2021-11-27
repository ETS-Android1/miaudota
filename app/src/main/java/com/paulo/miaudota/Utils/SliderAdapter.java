package com.paulo.miaudota.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.paulo.miaudota.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public int[] slide_images ={
            R.drawable.intro1,
            R.drawable.intro2,
            R.drawable.intro3
    };

    public String[] slide_texts ={
            "Faça seu cadastro rapidamente",
            "Procure entre os mais de 1000 pets cadastrados",
            "Adote um companheiro para seu dia"
    };

    public String[] slide_btn_texts ={
            "Próximo",
            "Próximo",
            "Continuar"
    };

    @Override
    public int getCount() {
        return slide_texts.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
       return view == (RelativeLayout) o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_item, container,false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.imageView);
        TextView slidetext = (TextView) view.findViewById(R.id.textSlide);

        slideImageView.setImageResource(slide_images[position]);
        slidetext.setText(slide_texts[position]);


        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((RelativeLayout)object);

    }
}
