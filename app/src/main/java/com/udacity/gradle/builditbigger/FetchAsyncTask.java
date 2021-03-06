package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.os.AsyncTask;
import com.brainbreaker.jokey.backend.myApi.MyApi;
import com.brainbreaker.jokey.backend.myApi.model.MyBean;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class FetchAsyncTask extends AsyncTask<Void, Void, String> {
    private static MyApi myApiService = null;
    private Context context;
    private TaskListener listener = null;
    private Exception error = null;

    public interface TaskListener {
        public void onComplete(String jokeString, Exception e);
    }

    public FetchAsyncTask(Context context) {
        this.context = context;
    }

    public FetchAsyncTask setListener(TaskListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (myApiService == null) {  // Only do this once
            // Prod Server Settings:
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://demoproject-28bd9.appspot.com/_ah/api/");

            // devappserver
            /* MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            */
            myApiService = builder.build();
        }

        try {
            return myApiService.setJoke(new MyBean()).execute().getJoke();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (this.listener != null)
            this.listener.onComplete(result, error);
    }
}