package flotu.tilepic;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by spier on 19.08.14.
 * Company: FLOTU
 * Dev: Syomin Stas
 * Product: Msgnr
 */
public class Tilepic extends BaseAdapter {
    // custom things
    private Integer oneline;
    private Integer max;
    private View.OnClickListener listener = null;
    private boolean flagLogs = false;
    private boolean flagPreloader = false;
    private boolean flagDownloadWait = false;
    private String TAG = "NAME";

    // privates things
    private Map<Integer, Boolean> flags;
    private List<List<MetaImage>> imagelist;
    private List<List<ImageView>> imageviewlist;
    private ConcurrentLinkedQueue<MetaImage> queue;
    private ExecutorService service = Executors.newSingleThreadExecutor();
    private Context context;
    private Handler handler, handlerNotify;
    private Integer width;

    private Boolean flag = true;

    // trying to do like picasso
    public static Tilepic with(Context context) {
        return new Tilepic(context);
    }

    public Tilepic into(ListView listView) {
        listView.setAdapter(this);
        return this;
    }

    public Tilepic put(List<String> urls) {
        for (String url : urls) {
            putUrl(url);
        }
        return this;
    }

    public Tilepic(Context context) {
        this.context = context;
        flags = new HashMap<>();
        flags.put(0, false);
        queue = new ConcurrentLinkedQueue<>();
        imagelist = new CopyOnWriteArrayList<>();
        imagelist.add(0, new CopyOnWriteArrayList<MetaImage>());
        imageviewlist = new ArrayList<>();


        getScreen();

        handlerNotify = new Handler() {
            public void handleMessage(Message msg) {
                notifyDataSetChanged();
            }
        };

        handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    printLog("WORKOUT-HANDLER-START############################");
                    Integer id = msg.arg2;
                    Integer newornot = msg.arg1;
                    List<MetaImage> list = (List<MetaImage>) msg.obj;

                    if (newornot == 1) {
                        imagelist.add(list);
                    } else {
                        imagelist.set(id, list);
                    }

                    // по добавлению в основную коллекцию ставим флаг тру
                    flag = true;


                    printLog("NEWEL: " + getImageCount() + " " + getCount() + " " + newornot + " " + list.toString());

                    printLog("WORKOUT-HANDLER-STOP############################");
                    printLog("COUNT-Handler: " + getCount());
                    notifyDataSetChanged();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                handlerNotify.sendEmptyMessage(1);
            }
        }).start();
    }

    private Tilepic(Context context, List<String> urls) {
        this.context = context;

        //TODO: обработка urls
    }

    // options

    /** set the width of view you are using and
     *  max height of one line with images
     *
     * @param width view width
     * @param heightOfOneLine one line msx height
     */
    public void setDimentions(Integer width, Integer heightOfOneLine) {
        this.width = width;
        this.oneline = heightOfOneLine;
    }

    /** set onclick listener for every image
     *
     * @param listener
     */
    public void setImageClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    /** set the max images in one line, no matter the dimensions is
     *
     * @param max
     */
    public void setMaxImageInLine(Integer max) {
        this.max = max;
    }

    /** so... enable logs
     *
     * @param b true - logs enabled, false - not
     */
    public void enableLogs(boolean b) {
        this.flagLogs = b;
    }

    public void waitUntillAllLoaded(boolean b) {
        // TODO: realise it
    }

    public void enamlePreloaders(boolean b) {
        // TODO: realise it
    }

    private void putUrl(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    printLog(url);
                    Bitmap bitmap = Picasso.with(context).load(url).get();
                    addToQueue(new MetaImage(url, bitmap.getHeight(), bitmap.getWidth()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.test_oneline, null);
        }

        printLog("COUNT: " + getCount());

        //LinearLayout layout = new LinearLayout(context);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.layout_main);

        printLog("))))))))))))))))))))))))))))))))))");
        printLog(imagelist.get(position).toString());
        printLog("))))))))))))))))))))))))))))))))))");

        Integer fullweidth = 0;
        List<Pair<ImageView, MetaImage>> pairs = new ArrayList<>();
        List<ImageView> cachelist;
        if (imageviewlist.size() > position) {
           cachelist = imageviewlist.get(position);
        } else {
            cachelist = new ArrayList<>();
            imageviewlist.add(position, cachelist);
        }
        layout.removeAllViews();
        for (int i = 0; i < imagelist.get(position).size(); i++) {
            MetaImage mi = imagelist.get(position).get(i);
            ImageView imageView;
            if (cachelist.size() > i) {
                imageView = cachelist.get(i);
            } else {
                imageView = new ImageView(context);
                cachelist.add(imageView);
            }
            fullweidth += mi.getlWidth();
            Picasso.with(context).load(mi.getUrl()).into(imageView);
            //Picasso.with(context).load(mi.getUrl()); // ?? what is it for?

            if (listener != null) {
                imageView.setOnClickListener(listener);
            }

            pairs.add(new Pair<>(imageView, mi));
            layout.addView(imageView);
        }

        Integer h = 0;
        for (Pair<ImageView, MetaImage> imagePair : pairs) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imagePair.first.getLayoutParams();
            params.height = imagePair.second.getlHeigth();
            params.width = imagePair.second.getlWidth();
            h = imagePair.second.getlHeigth();
            imagePair.first.setLayoutParams(params);
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.height = h;
        layout.setLayoutParams(params);

        printLog("=========================");
        for (List<MetaImage> list : imagelist) {
            printLog(Integer.toString(list.size()));
        }
        printLog("=========================");

        return v;
    }

    @Override
    public int getCount() {
        return imagelist.size();
    }

    @Override
    public List<MetaImage> getItem(int position) {
        return imagelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void addToQueue(MetaImage metaImage) {
        // TODO: delete this thing
        queue.offer(metaImage);
        startConsumer();
    }

    private void startConsumer() {
        printLog("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        printLog("SIZE: " + queue.size() + " " + queue.toString());
        service.submit(new TestThread());
    }

    private void getScreen() {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        Integer height = size.y;
        oneline = height / 4;
        printLog("TEST: height " + oneline);
    }

    private Integer parseFloat(Float f) {
        String str = f.toString();
        return Integer.parseInt(str.substring(0, str.indexOf(".")));
    }

    private int getImageCount() {
        Integer count = 0;
        for (List<MetaImage> images : imagelist) {
            count += images.size();
        }
        return count;
    }

    private void printLog(String logMessage) {
        if (flagLogs) {
            Log.v(TAG, logMessage);
        }
    }

    class TestThread implements Runnable {

        @Override
        public void run() {
            Integer size = queue.size();
            for (int i = 0; i < size; i++) {
                while (!flag) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    printLog("TEST sleep");
                }

                //1 достать следующий элемент из очереди
                MetaImage metaImage = queue.poll();
                printLog("TEST: new image " + metaImage.toString());

                //2 узнать завершена ли последняя строка
                Integer lastline = imagelist.size() - 1;

                printLog("TEST: ll pos " + lastline);

                Boolean done = flags.get(lastline);

                printLog("TEST: ll flag " + done);

                List<MetaImage> images;
                //3 если завершено - создать новый массив и закинуть в него элемент
                if (done) {
                    printLog("TEST: line new");
                    images = new ArrayList<>();
                    images.add(metaImage);
                    lastline++;
                }

                //4 если не завершена - сделать копию последней строки и кинуть в нее последний элемент
                else {
                    images = new CopyOnWriteArrayList<>(imagelist.get(lastline));
                    printLog("TEST: line last " + images.toString());
                    images.add(metaImage);
                }


                //5 пересчитать размеры для всех элементов
                Float sum = 0f;
                for (MetaImage mi : images) {
                    sum = sum + mi.getImageInfo();
                }

                Float newheight = width / sum;

                if (newheight > oneline) {
                    newheight = oneline + 1f;
                }

                for (MetaImage mi : images) {
                    Float newwidth = newheight * mi.getImageInfo();
                    mi.setlHeigth(parseFloat(newheight));
                    mi.setlWidth(parseFloat(newwidth));
                }

                printLog("TEST: new h " + newheight);

                //6 пересчитать строку и поместить новый флаг
                if (newheight > (oneline)) {
                    flags.put(lastline, false);
                    printLog("TEST: new flag " + lastline + " " + false);
                } else {
                    flags.put(lastline, true);
                    printLog("TEST: new flag " + lastline + " " + true);
                }


                printLog("TEST: " + getImageCount() + " " + getCount() + " " + (done ? 1 : 0) + " " + images.toString());

                flag = false;

                //7 отправить строку с флагом следующая или текущая через хендлер
                Message msg = new Message();
                msg.arg1 = done ? 1 : 0;
                msg.arg2 = lastline;
                msg.obj = images;
                handler.sendMessage(msg);
            }
        }
    }

    class MetaImage {
        private String url;
        private Integer heigth;
        private Integer width;

        private Integer lHeigth;
        private Integer lWidth;

        MetaImage(String url, Integer heigth, Integer width) {
            this.url = url;
            this.heigth = heigth;
            this.width = width;
        }

        public Float getImageInfo() {
            return Float.parseFloat(this.width.toString()) / Float.parseFloat(this.heigth.toString());
        }

        public Integer getlHeigth() {
            return lHeigth;
        }

        public void setlHeigth(Integer lHeigth) {
            this.lHeigth = lHeigth;
        }

        public Integer getlWidth() {
            return lWidth;
        }

        public void setlWidth(Integer lWidth) {
            this.lWidth = lWidth;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeigth() {
            return heigth;
        }

        public void setHeigth(Integer heigth) {
            this.heigth = heigth;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "MetaImage{" +
                    "url='" + url + '\'' +
                    ", heigth=" + heigth +
                    ", width=" + width +
                    ", lHeigth=" + lHeigth +
                    ", lWidth=" + lWidth +
                    '}';
        }
    }
}
