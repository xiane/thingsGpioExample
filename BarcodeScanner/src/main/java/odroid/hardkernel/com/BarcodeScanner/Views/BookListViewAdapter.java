package odroid.hardkernel.com.BarcodeScanner.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardkernel.com.BarcodeScanner.R;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class BookListViewAdapter extends ArrayAdapter<BookItemView> {
    public BookListViewAdapter(@NonNull Context context, ArrayList<BookItemView> arrayList) {
        super(context, 0, arrayList);
    }

    @Override
    public void add(@Nullable BookItemView object) {
        super.add(object);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentItemView = convertView;

        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_item_view, parent, false);
        }

        BookItemView currentBookPosition = getItem(position);
        ImageView bookThumbnail = currentItemView.findViewById(R.id.ThumbnailView);
        assert currentBookPosition!= null;
        final Bitmap[] thumbnail = {null};
        try {
            Thread downloadImg = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(currentBookPosition.getImageUrl());
                        InputStream in = url.openConnection().getInputStream();
                        thumbnail[0] = BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            downloadImg.start();
            downloadImg.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bookThumbnail.setImageBitmap(thumbnail[0]);

        TextView bookTitle = currentItemView.findViewById(R.id.BookTitle);
        bookTitle.setText(currentBookPosition.getBookTitle());

        TextView bookDescription = currentItemView.findViewById(R.id.BookDesc);
        bookDescription.setText(currentBookPosition.getBookDesc());

        return currentItemView;
    }
}
