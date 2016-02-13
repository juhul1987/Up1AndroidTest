package com.example.exito.uptestnativeapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Ankit on 05-02-2016.
 */
public class ProductActivity extends AppCompatActivity {

    private GridView gridView_product;
    public static String [] prgmNameList={"$12.50","$100","$50","$45.60","$34.90","$23","$1000","$99","$88.77"};
    public static int [] prgmImages={R.drawable.app_logo,R.drawable.back_arrow,R.drawable.app_logo,R.drawable.singup_bottom,R.drawable.app_logo,R.drawable.app_logo,R.drawable.singup_bottom,R.drawable.app_logo,R.drawable.singup_bottom};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Intent intent = getIntent();
        gridView_product = (GridView) findViewById(R.id.gridView_product);

        gridView_product.setAdapter(new CustomAdapter(ProductActivity.this, prgmNameList,prgmImages));

    }

    public class CustomAdapter extends BaseAdapter {
        private String[] result;
        private Context mcontext;
        private int[] imageId;
        private LayoutInflater inflater = null;

        public CustomAdapter(Context context, String[] prgmNameList, int[] prgmImages) {
            // TODO Auto-generated constructor stub
            result = prgmNameList;
            mcontext = context;
            imageId = prgmImages;
            inflater = (LayoutInflater) mcontext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return result.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            Holder holder =new Holder();
            View rowView = convertView;
            if (rowView == null) {
                rowView = inflater.inflate(R.layout.activity_product_item, null);
                holder.tv = (TextView) rowView.findViewById(R.id.txt_gride);
                holder.img = (ImageView)rowView.findViewById(R.id.image_gride);
                holder.tv.setText(result[position]);
                holder.img.setImageResource(imageId[position]);
                holder.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext() ,OrderActivity.class);
                        intent.putExtra("result" ,result[position]);
                        startActivity(intent);
                        finish();
                    }
                });

            } else {
                rowView = (View) convertView;
            }

            return rowView;
        }
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProductActivity.this,SplaceSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
