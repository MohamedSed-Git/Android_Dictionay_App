package owlbotdictionary;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import com.androidproject.owlbotdictionary.R;

class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    Context context;
    List<String> data;
    String val;
    JSONArray defsArray;

            View.OnClickListener mClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    TextView textView = (TextView) view;
                    String def_ = (String) textView.getText();
                    Intent intent = new Intent(context, Information.class);
                    JSONObject obj = new JSONObject();

                    try {

                        JSONArray defsArray = (JSONArray) obj.get("definitions");

                        for (int i = 0; i < defsArray.length(); i++) {

                            String def = defsArray.getJSONObject(i).get("definition").toString();
                            if (def.contains(def_.replace("...", ""))) {
                                intent.putExtra("def", def);

                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    context.startActivity(intent);
                }
            };


    public RecyclerAdapter(List<String> data, JSONObject obj, Context context) {

        this.data = data;

        try {
            defsArray = (JSONArray) obj.get("definitions");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.context = context;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {//implements View.OnClickListener{

        Context mContext;
        TextView textView;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.mContext = context;
            textView = itemView.findViewById(R.id.textNames);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_itemview, parent, false);
        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        val = data.get(position);
        holder.textView.setText(val);
        holder.textView.setOnClickListener(v -> {
            if (defsArray != null) {
                try {
                    Intent intent = new Intent(context, Information.class);

                    JSONObject obj = (JSONObject) defsArray.get(position);

                    String def = obj.getString("definition");
                    String type = obj.getString("type");
                    String example = obj.getString("example");

                    intent.putExtra("def", def);
                    intent.putExtra("type", type);
                    intent.putExtra("example", example);

                    context.startActivity(intent);

                } catch (JSONException e) {
                    Toast.makeText(context, "Some error occurred.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else
                Toast.makeText(context, "Try again later.", Toast.LENGTH_SHORT).show();

        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}