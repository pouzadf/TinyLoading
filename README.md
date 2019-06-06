# TinyLoading

TinyLoading is a flexible and reliable asynchronous image loading library. It provides simple streamlined public API for a better user experience. TinyLoading implements caching and thrading logic in order to load as fast as possible requested images. Images can be requested from Urls, Files or Resources.

# Compatibility
* TinyLoading is available for Android SDK 15 and above.

# Usage
A simple usage would be to load an image from an url into a specific view.

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView iw = findViewById(R.id.myView);
        TinyLoading
                .get()
                .with(this)
                .load("http://mysource.com")
                .into(iw);

    }
}
```

An advanced usage would be to load images while scrolling a list view.
```java
public View getView(final int position, View convert_view, ViewGroup parent) {
        ViewHolder holder;
        if (convert_view == null) 
		{
            holder = new ViewHolder();
            // get access to the layout infaltor service
            LayoutInflater inflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convert_view = inflator.inflate(R.layout.list_item, parent, false);

            // pull all the items from the XML so we can modify them
            holder.l_picture =  convert_view.findViewById(R.id.left_image);
        } 
		else {
            holder = (ListViewAdapter.ViewHolder) convert_view.getTag();
        }
		TinyLoading.get()
                    .with(this.c)
                    .load(sourceList.get(position))
                    .into(holder.l_picture);
					
        return convert_view;
    }
```

### Cancel
TinyLoading provides an easy way to cancel any request targeting specific View.
```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView iw = findViewById(R.id.myView);
        TinyLoading
                .get()
                .with(this)
                .load("http://mysource.com")
                .into(iw);
				
		//cancel previous request
		TinyLoading
                .get()
                .cancel(iw);
    }
}
```
### Fallback
User can set a default image to fill the view with if the fetching request fails.

```java
Drawable replacement = ContextCompat.getDrawable(c,R.drawable.id)
        ImageView iw = findViewById(R.id.myView);
		
		//Will dispplay replacement drawable
        TinyLoading
                .get()
                .with(this)
                .fallback(d)
                .load("willfail")
                .into(iw);
````
More features available in the [documentation](http://134.209.64.203/).

# Example
A simple example is available in TinyLoadingExample directory.

# Author
Florian Pouzada

