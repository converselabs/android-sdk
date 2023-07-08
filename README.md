# Android SDK Integration (`android-web-view-url-lib`)
## Steps

1. Copy the `android-web-view-url-lib-debug.aar` file to the libs folder in your app module.
2. Open your app-level `build.gradle` file and add the following line inside the dependencies block:
```java
implementation files('libs/android-web-view-url-lib-debug.aar')
```
3. Sync your project with the Gradle files to ensure the library is successfully added to your project.
4. Open your `MainActivity.java` file and import the required classes:
```java
import com.example.android_web_view_url_lib.WebUrlView;
```
5. In your activity's layout file (`activity_main.xml`), make sure you have a `Button` and a `LinearLayout` container as defined in your code:
```java
<Button
    android:id="@+id/openWebViewButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Open Web View" />

<LinearLayout
    android:id="@+id/webUrlContainer"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:visibility="gone" />
```

6. In your `MainActivity.java`, declare the necessary variables:
```java
private Button openWebViewButton;
private LinearLayout webUrlContainer;
private WebUrlView webUrlView;
```
7. In the `onCreate()` method of your `MainActivity`, initialize the button and container views:
```java
openWebViewButton = findViewById(R.id.openWebViewButton);
webUrlContainer = findViewById(R.id.webUrlContainer);
```
8. Set a click listener on the "Open Web View" button to handle the WebView initialization:
```java
openWebViewButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (webUrlView != null) {
            // Remove the existing WebUrlView if present
            webUrlContainer.removeView(webUrlView);
            webUrlView.destroyView();
        }

        // Create a new instance of WebUrlView
        webUrlView = new WebUrlView(MainActivity.this);
        webUrlView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        webUrlView.setWebViewCallback(MainActivity.this);
        String url = "https://your-website-url.com";  // Replace with your desired URL
        webUrlView.loadUrl(url);
        webUrlContainer.addView(webUrlView);

        // Show the webUrlContainer and trigger a layout pass
        webUrlContainer.setVisibility(View.VISIBLE);
        webUrlContainer.requestLayout();
    }
});
```
Make sure to replace `"https://your-website-url.com"` with the chatbot URL you want to load in the WebView.

9. Implement the `WebViewCallback` interface in your `MainActivity` to handle WebView events: 
```java
public class MainActivity extends AppCompatActivity implements WebUrlView.WebViewCallback {
    // ...

    @Override
    public void onViewClosed() {
        // Remove the WebUrlView from the container
        if (webUrlView != null) {
            webUrlContainer.removeView(webUrlView);
            webUrlView.destroyView();
            webUrlView = null;
            webUrlContainer.setVisibility(View.GONE);
        }
    }
}
```
10. Run your app and test the `"Open Web View"` button. It should open the WebView and load the specified chatbot URL.
