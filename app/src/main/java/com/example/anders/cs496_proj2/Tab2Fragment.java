package com.example.anders.cs496_proj2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class Tab2Fragment extends Fragment {

    final public static ArrayList<String> gotten = new ArrayList<String>();
    private static GridView gridView;
    private static GridViewAdapter gridViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab2fragment, container, false);

        // TODO getPhotos (save to bitmaps)
        try {
            new getPhotos().execute().get();
        } catch(Exception e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Add a Photo").setCancelable(true).setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getActivity(),
                                "com.example.android.fileprovider",
                                photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, 1);
                    }
                }
            }
        }).setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), 2);
            }
        });

        gridView = (GridView) view.findViewById(R.id.gridView);
        gridViewAdapter = new GridViewAdapter(getActivity());
        gridView.setAdapter(gridViewAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity().getApplicationContext(), ImageSliderActivity.class);
                i.putExtra("id", position);
                startActivity(i);
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO removePhoto
                new removePhoto().execute(GridViewAdapter.bitmaps.get(position).getId());
                GridViewAdapter.bitmaps.remove(position);
                gridViewAdapter.notifyDataSetChanged();
                gridView.invalidateViews();
                return true;
            }
        });

        return view;
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".png", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            Bitmap photo = null;
            if(requestCode == 1) {
                //photo = (Bitmap) data.getExtras().get("data");
                //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                //photo.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                /* File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch(FileNotFoundException e) {
                    e.printStackTrace();
                } catch(IOException e) {
                    e.printStackTrace();
                } */
                photo = BitmapFactory.decodeFile(mCurrentPhotoPath);
                try {
                    ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotateImage(photo, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotateImage(photo, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotateImage(photo, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:

                        default:
                            break;
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            } else if(requestCode == 2) {
                if(data != null) {
                    try {
                        photo = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), data.getData());
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(photo.getWidth() > 2048) {
                photo = Bitmap.createScaledBitmap(photo, 2048, photo.getHeight() * 2048 / photo.getWidth(), true);
            }
            if(photo.getHeight() > 2048) {
                photo = Bitmap.createScaledBitmap(photo, photo.getWidth() * 2048 / photo.getHeight(), 2048, true);
            }

            // TODO addPhoto
            PackedImage packedPhoto = new PackedImage(GridViewAdapter.nextId, photo);
            new addPhoto().execute(packedPhoto);
            GridViewAdapter.bitmaps.add(0, packedPhoto);
            GridViewAdapter.nextId++;
            gridViewAdapter.notifyDataSetChanged();

        }
    }

    private String getStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private Bitmap getBitmapFromString(String string) {
        byte[] b = Base64.decode(string, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }
    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public class getPhotos extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL("http://ec2-52-79-95-160.ap-northeast-2.compute.amazonaws.com:3000/get_photos");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuffer response = new StringBuffer();
                String input;
                while((input = br.readLine()) != null) {
                    response.append(input);
                }
                br.close();
                JSONArray jsonMainNode = new JSONArray(response.toString());
                for(int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    Integer id = Integer.parseInt(jsonChildNode.getString("id"));
                    String extracted = jsonChildNode.getString("image");
                    Bitmap decoded = getBitmapFromString(extracted);
                    GridViewAdapter.bitmaps.add(new PackedImage(id, decoded));
                    if(GridViewAdapter.nextId < id) {
                        GridViewAdapter.nextId = id;
                    }
                    Collections.sort(GridViewAdapter.bitmaps, new CustomComparator());
                }
                GridViewAdapter.nextId++;
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {

        }

        @Override
        protected void onPostExecute(Void result) {
            gridViewAdapter.notifyDataSetChanged();
            gridView.invalidateViews();
        }
    }

    public class addPhoto extends AsyncTask<PackedImage, Void, Void> {

        @Override
        protected Void doInBackground(PackedImage... params) {
            try {
                URL url = new URL("http://ec2-52-79-95-160.ap-northeast-2.compute.amazonaws.com:3000/add_photo");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                OutputStream os = conn.getOutputStream();
                String stringified = getStringFromBitmap(params[0].getBitmap());
                JSONObject jsonified = new JSONObject();
                jsonified.put("id", params[0].getId());
                jsonified.put("image", stringified);
                String output = jsonified.toString();
                os.write(output.getBytes());
                os.flush();
                os.close();
                conn.connect();
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }
                conn.disconnect();
            } catch(Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {

        }

        @Override
        protected void onPostExecute(Void result) {
            gridView.invalidateViews();
        }
    }

    public class removePhoto extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            try {
                URL url = new URL("http://ec2-52-79-95-160.ap-northeast-2.compute.amazonaws.com:3000/remove_photo/?id=" + Integer.toString(params[0]));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {

        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

    private class CustomComparator implements Comparator<PackedImage> {
        @Override
        public int compare(PackedImage o1, PackedImage o2) {
            return o2.getId().compareTo(o1.getId());
        }
    }
}
