package morcom.christopher.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsFragment extends Fragment {

    private static final String ARTICLE= "ARTICLE";
    private static final String COUNT= "COUNT";
    private static final String TAG = "NewsFragment";

    public static NewsFragment newInstance(NewsArticle article, String count) {

        Bundle args = new Bundle();

        NewsFragment fragment = new NewsFragment();
        args.putSerializable(ARTICLE, article);
        args.putString(COUNT, count);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final NewsArticle newsArticle = (NewsArticle) getArguments().getSerializable(ARTICLE);
        String c = getArguments().getString(COUNT);
        View view = inflater.inflate(R.layout.news_fragment, container, false);
        ConstraintLayout fragmentRoot = view.findViewById(R.id.fragmentConstraint);
        TextView headline = view.findViewById(R.id.Title);
        TextView date = view.findViewById(R.id.publishTime);
        TextView author = view.findViewById(R.id.Author);
        TextView description = view.findViewById(R.id.Text);
        TextView count = view.findViewById(R.id.Count);
        final ImageView image = view.findViewById(R.id.Image);

        count.setText(c);

        if(newsArticle.getTitle()!= null && !newsArticle.getTitle().equals("null")) {
            headline.setText(newsArticle.getTitle());
            headline.setMovementMethod(LinkMovementMethod.getInstance());
            headline.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(newsArticle.getUrl()));
                    startActivity(browserIntent);
                }
            });
        } //else { fragmentRoot.removeView(headline); }
        if(newsArticle.getAuthor()!=null && !newsArticle.getAuthor().equals("null")) {
            author.setText(newsArticle.getAuthor());
            author.setMovementMethod(LinkMovementMethod.getInstance());
            author.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(newsArticle.getUrl()));
                    startActivity(browserIntent);
                }
            });
        } //else { fragmentRoot.removeView(author); }
        if(newsArticle.getPublishedAt()!=null && !newsArticle.getPublishedAt().equals("null")){
            SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            try {
                Date now = readFormat.parse(newsArticle.getPublishedAt());
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                date.setText(sdf.format(now));
                Log.d(TAG, "onCreateView: DATE: "+date.getText());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } //else { fragmentRoot.removeView(date); }
        if(newsArticle.getDescription()!=null && !newsArticle.getDescription().equals("null")){
            description.setText(newsArticle.getDescription());
            description.setMovementMethod(LinkMovementMethod.getInstance());
            description.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(newsArticle.getUrl()));
                    startActivity(browserIntent);
                }
            });
        } //else { fragmentRoot.removeView(description); }
        if(newsArticle.getUrlToImage()!= null && !newsArticle.getUrlToImage().equals("null")){
            Picasso picasso = new Picasso.Builder(getContext()).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    final String changedUrl = newsArticle.getUrlToImage().replace("http:", "https:");
                    picasso.load(changedUrl)
                            .fit()
                            .centerInside()
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(image);
                }
            }).build();
            picasso.load(newsArticle.getUrlToImage())
                    .fit()
                    .centerInside()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(image);

            image.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(newsArticle.getUrl()));
                    startActivity(browserIntent);
                }
            });
        }
        return view;
    }
}
