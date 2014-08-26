package flotu.tilepic;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ListView list = (ListView) findViewById(R.id.listview);

        List<String> urls = new ArrayList<>();
        urls.add("http://lorempixel.com/401/400/");
        urls.add("http://lorempixel.com/402/600/");
        urls.add("http://lorempixel.com/403/800/");
        urls.add("http://lorempixel.com/804/400/");
        urls.add("http://lorempixel.com/605/400/");
        urls.add("http://lorempixel.com/206/400/");
        urls.add("http://lorempixel.com/507/300/");
        urls.add("http://lorempixel.com/708/500/");
        urls.add("http://lorempixel.com/809/400/");
        urls.add("http://lorempixel.com/610/400/");
        urls.add("http://lorempixel.com/211/400/");
        urls.add("http://lorempixel.com/512/300/");
        urls.add("http://lorempixel.com/413/800/");
        urls.add("http://lorempixel.com/814/400/");
        urls.add("http://lorempixel.com/615/400/");
        urls.add("http://lorempixel.com/216/400/");
        urls.add("http://lorempixel.com/517/300/");

        Tilepic.with(this).put(urls).into(list);
    }
}
