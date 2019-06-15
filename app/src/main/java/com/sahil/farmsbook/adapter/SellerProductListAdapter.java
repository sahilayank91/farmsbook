package com.sahil.farmsbook.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.Views.CartActivity;
import com.sahil.farmsbook.Views.ListActivity;
import com.sahil.farmsbook.interfaces.RCVItemClickListener;
import com.sahil.farmsbook.model.Product;
import com.sahil.farmsbook.model.Unit;
import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;

public class SellerProductListAdapter extends RecyclerView.Adapter<SellerProductListAdapter.ProductViewHolder> implements RCVItemClickListener, AdapterView.OnItemSelectedListener {
    public ArrayList<Product> listProduct;
    private Context context;
    private String type;
    SharedPreferences sharedpreferences;
    public SellerProductListAdapter(Context c,ArrayList<Product> listProduct){
        this.context = c;
        this.listProduct = listProduct;
    }
    public SellerProductListAdapter(Context c,ArrayList<Product> listProduct, String type){
        this.context = c;
        this.listProduct = listProduct;
        this.type = type;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        final Product current = listProduct.get(position);

        Gson gson = new Gson();

        if(type!=null && type.equals("List")){
            holder.deleteButton.setVisibility(View.GONE);
            current.setQuantity("0");
            holder.total.setText("Rs 0");
        }

        if(current.getOutofstock().equals("true")){
            holder.total.setText("Out of Stock");
            holder.minus.setVisibility(View.GONE);
            holder.plus.setVisibility(View.GONE);
            holder.quantity.setVisibility(View.GONE);

        }else{
            holder.minus.setVisibility(View.VISIBLE);
            holder.plus.setVisibility(View.VISIBLE);
            holder.quantity.setVisibility(View.VISIBLE);
        }

        if(current.getOutofstock()==null){
            holder.minus.setVisibility(View.VISIBLE);
            holder.plus.setVisibility(View.VISIBLE);
            holder.quantity.setVisibility(View.VISIBLE);

        }



        final List<String> categories = new ArrayList<String>();


        final HashMap<String,String> param = new HashMap<>();

//    if(current.getUnitlist()!=null){
//
//        for(int i=0;i<current.getUnitlist().size();i++){
//            Unit unit = current.getUnitlist().get(i);
//
//            param.put("Rs "+ unit.getPrice() + " - " + unit.getQuantity()+unit.getUnit(),unit.getPrice());
//            categories.add("Rs "+ unit.getPrice() + " - " + unit.getQuantity()+unit.getUnit());
//        }
//
//    }

        if(type.equals("Cart")){
            if(current.getUnits()!=null){
                Log.e("uni",current.getUnits());
                try {
                    JSONArray jsonArray = new JSONArray(current.getUnits());
                    Log.e("arr:",jsonArray.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
        if(current.getUnits()!=null){
            try {
                JSONArray jsonArray = new JSONArray(current.getUnits());

                for(int i=0;i<jsonArray.length();i++){
                    Unit unit = new Unit(jsonArray.getJSONObject(i));
                    param.put("Rs "+ unit.getPrice() + " - " + unit.getQuantity()+unit.getUnit(),unit.getPrice());
                    categories.add("Rs "+ unit.getPrice() + " - " + unit.getQuantity()+unit.getUnit());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        holder.spinner.setAdapter(dataAdapter);

        holder.spinner.setSelected(true);



        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                current.setPrice(param.get(adapterView.getItemAtPosition(position).toString()));
                current.setUnit(adapterView.getItemAtPosition(position).toString());
                if(current.getOutofstock().equals("false"))
                holder.total.setText("Rs " + Integer.parseInt(param.get(adapterView.getItemAtPosition(position).toString()))*Integer.parseInt(current.getQuantity()));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        holder.spinner.setSelection(0);
//        current.setPrice(param.get(holder.spinner.getSelectedItem().toString()));


        if(type.equals("Cart")){
            holder.total.setText("Rs " + current.getTotal());
        }

        holder.name.setText(current.getName());

//        holder.price.setText(current.getPrice());

        holder.brand.setText("Brand - " + current.getBrand());
        holder.hindiname.setText("(" + current.getHindiname() + ")");

        if(current.getQuantity()!=null){
            holder.quantity.setText(current.getQuantity());
        }else{
            holder.quantity.setText("0");
        }

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Integer p = Integer.parseInt(holder.quantity.getText().toString());
                p--;
                if(p<0){
                    Toast.makeText(context,"Can't decrease Value", Toast.LENGTH_SHORT).show();
                    try {
                        removeFromCart(current, position);
                        if(context instanceof CartActivity){
                            ((CartActivity)context).updateTotal();
                        }
                        if(context instanceof ListActivity){
                            ((ListActivity)context).update();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{
                    holder.quantity.setText(String.valueOf(p));
                    current.setQuantity(String.valueOf(p));
                    current.setTotal(String.valueOf(Integer.parseInt(current.getPrice())*Integer.parseInt(current.getQuantity())));
                    holder.total.setText("Rs " + current.getTotal());
                    if(context instanceof CartActivity){
                        ((CartActivity)context).updateTotal();
                    }
                    if(context instanceof ListActivity){
                        ((ListActivity)context).update();
                    }
                }

            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer p = Integer.parseInt(holder.quantity.getText().toString());
                p++;

                    holder.quantity.setText(String.valueOf(p));
                    current.setQuantity(String.valueOf(p));
                    current.setTotal(String.valueOf(Integer.parseInt(current.getPrice())*Integer.parseInt(current.getQuantity())));
                    holder.total.setText("Rs " + current.getTotal());


                try {
                    addToCart(current);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(context instanceof CartActivity){
                    ((CartActivity)context).updateTotal();
                }
                if(context instanceof ListActivity){
                    ((ListActivity)context).update();
                }
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    removeFromCart(current, position);
                    if(context instanceof CartActivity){
                        ((CartActivity)context).updateTotal();
                    }
                    if(context instanceof ListActivity){
                        ((ListActivity)context).update();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Glide.with(context).load(current.getImageurl()).into(holder.image);

    }


    private void removeFromCart(Product product, Integer position   ) throws JSONException {
        Gson gson = new Gson();

        String cart = SharedPreferenceSingleton.getInstance(context).getString("cart");

        java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        HashMap<String, String> cartmap = gson.fromJson(cart, type);

        if(cartmap.containsKey(product.get_id())){
            cartmap.remove(product.get_id());
            SharedPreferenceSingleton.getInstance(context).put("cart", gson.toJson(cartmap));
        }
        if(context instanceof CartActivity){
            listProduct.remove(product);
            if(listProduct.size()==0){
                ((Activity)context).finish();
            }
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listProduct.size());
        }


    }
    private void addToCart(Product product) throws JSONException {
       String cart = SharedPreferenceSingleton.getInstance(context).getString("cart");

        Gson gson = new Gson();
        String json  = gson.toJson(product);

        HashMap<String, String> param = new HashMap<String, String>();
        if(cart==null){
            param.put(product.get_id(),json);
            String js = gson.toJson(param);
            SharedPreferenceSingleton.getInstance(context).put("cart", js);
            Toast.makeText(context,"Item added to the Cart",Toast.LENGTH_SHORT).show();
        }else{
            String cartString = SharedPreferenceSingleton.getInstance(context).getString("cart","Can't find the value");
            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
            HashMap<String, String> cartmap = gson.fromJson(cartString, type);

            cartmap.put(product.get_id(),json);
            SharedPreferenceSingleton.getInstance(context).put("cart", gson.toJson(cartmap));
            Toast.makeText(context,"Item added to the Cart",Toast.LENGTH_SHORT).show();
        }

    }

//    private void openCancelDialog(View view, final int position){
//        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//        alertDialogBuilder.setMessage("Are you sure you want to Delete the Product");
//        alertDialogBuilder.setIcon(R.drawable.newicon);
//        alertDialogBuilder.setPositiveButton("yes",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//
//                        Toast.makeText(context,"You have accepted to cancel the Order !!",Toast.LENGTH_LONG).show();
//                        new DeleteProduct(listProduct.get(position).get_id()).execute();
//
//                    }
//                });
//
//        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//
//
//            }
//        });
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView  total, name,brand, hindiname,quantity;
        ImageView minus, plus, image;
        Spinner spinner;
        ImageView deleteButton;
        private ProductViewHolder(View itemView) {
            super(itemView);

            total = itemView.findViewById(R.id.unit);
            name = itemView.findViewById(R.id.name);
            hindiname = itemView.findViewById(R.id.hindiname);
            brand = itemView.findViewById(R.id.brandName);
            quantity = itemView.findViewById(R.id.quantity);
            spinner  = itemView.findViewById(R.id.spinner);
            minus = itemView.findViewById(R.id.minus);
            plus = itemView.findViewById(R.id.add);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            image = itemView.findViewById(R.id.imageView);

        }


        @Override
        public void onClick(View v) {

        }
    }
    public void filterList(ArrayList<Product> filterdNames) {
        this.listProduct = filterdNames;
        notifyDataSetChanged();
    }
//    @SuppressLint("StaticFieldLeak")
//    class DeleteProduct extends AsyncTask<String, String, String> {
//        boolean success = false;
//        HashMap<String, String> params = new HashMap<>();
//        private ProgressDialog progress;
//
//        String pid;
//        DeleteProduct(String id){
//            this.pid = id;
//        }
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            params.put("_id",pid);
//            progress=new ProgressDialog(context);
//            progress.setMessage("Deleting Product");
//            progress.setIndeterminate(true);
//            progress.setProgress(ProgressDialog.STYLE_HORIZONTAL);
//            progress.show();
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            progress.dismiss();
//                Intent intent = new Intent(context, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                context.startActivity(intent);
//
//            ((Activity)context).finish();
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String result = "";
//            try {
//                Gson gson = new Gson();
//                String json = gson.toJson(params);
//                System.out.println(json);
//                result = Server.post(context.getResources().getString(R.string.deleteProduct),json);
//                success = true;
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//
//
//
//            System.out.println("Result:" + result);
//            return result;
//        }
//    }
}
