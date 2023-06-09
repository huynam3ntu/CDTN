package ntu.nthuy.recipeapp.Adapters;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ntu.nthuy.recipeapp.Model.ExtendedIngredient;
import ntu.nthuy.recipeapp.R;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsViewHolder>{

    Context context;
    ArrayList<ExtendedIngredient> list;

    public IngredientsAdapter(Context context, ArrayList<ExtendedIngredient> list) {
        this.context = context;
        this.list = list;
    }

    public ArrayList<ExtendedIngredient> getIngredient() {
        return list;
    }
    public void addIngredient(ExtendedIngredient ingredient) {

        list.add(ingredient);
        notifyItemInserted(list.size() - 1);
    }
    @NonNull
    @Override
    public IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IngredientsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_meal_ingredients, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsViewHolder holder, int position) {
        ExtendedIngredient extendedIngredient = list.get(position);
        // Hiển thị thông tin của đối tượng lên layout
        holder.textView_ingredients_name.setText(list.get(position).name);
        holder.textView_ingredients_name.setSelected(true);
        holder.textView_ingredients_quantity.setText(list.get(position).original);
        holder.textView_ingredients_quantity.setSelected(true);
        Picasso.get().load("https://spoonacular.com/cdn/ingredients_100x100/" + list.get(position).image).into(holder.imageView_ingredients);

        // Thiết lập sự kiện chỉnh sửa cho từng view
        holder.textView_ingredients_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    extendedIngredient.setName(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
        holder.textView_ingredients_quantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    extendedIngredient.setOriginal(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
        // Image?
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class  IngredientsViewHolder extends RecyclerView.ViewHolder {
    TextView textView_ingredients_quantity, textView_ingredients_name;
    ImageView imageView_ingredients;
    public IngredientsViewHolder(@NonNull View itemView) {
        super(itemView);
        textView_ingredients_quantity = itemView.findViewById(R.id.textView_ingredients_quantity);
        textView_ingredients_name = itemView.findViewById(R.id.textView_ingredients_name);
        imageView_ingredients = itemView.findViewById(R.id.imageView_ingredients);
    }
}
