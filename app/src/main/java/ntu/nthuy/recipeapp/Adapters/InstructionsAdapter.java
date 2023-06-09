package ntu.nthuy.recipeapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ntu.nthuy.recipeapp.Model.InstructionsReponse;
import ntu.nthuy.recipeapp.R;

public class InstructionsAdapter extends RecyclerView.Adapter<InstructionsViewHolder>{

    Context context;
    ArrayList<InstructionsReponse> list;

    public InstructionsAdapter(Context context, ArrayList<InstructionsReponse> list) {
        this.context = context;
        this.list = list;
    }
    public ArrayList<InstructionsReponse> getInstructions() {
        return list;
    }
    public void addSteps(InstructionsReponse reponse) {
        list.add(reponse);
        notifyItemInserted(list.size() - 1);
    }

    @NonNull
    @Override
    public InstructionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instructions, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionsViewHolder holder, int position) {

        holder.textView_instruction_name.setText(list.get(position).name);
        holder.recyler_íntruction_steps.setHasFixedSize(true);
        holder.recyler_íntruction_steps.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        InstructionStepAdapter stepAdapter = new InstructionStepAdapter(context, list.get(position).steps);
        holder.recyler_íntruction_steps.setAdapter(stepAdapter);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class InstructionsViewHolder extends RecyclerView.ViewHolder {
    TextView textView_instruction_name;
    RecyclerView recyler_íntruction_steps;

    public InstructionsViewHolder(@NonNull View itemView) {
        super(itemView);
        textView_instruction_name = itemView.findViewById(R.id.textView_instruction_name);
        recyler_íntruction_steps = itemView.findViewById(R.id.recyler_íntruction_steps);

    }
}
