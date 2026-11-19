package vhu.edu.edusync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder> {

    private final List<GradeItem> gradeList;

    public GradeAdapter(List<GradeItem> gradeList) {
        this.gradeList = gradeList;
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade, parent, false);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        GradeItem item = gradeList.get(position);
        holder.tvSubjectName.setText(item.getSubjectName());
        holder.tvGrade.setText(item.getGrade());
        holder.tvCredits.setText(item.getCredits());

        // Hide bottom divider for the last row
        if (position == gradeList.size() - 1) {
            holder.dividerBottom.setVisibility(View.GONE);
        } else {
            holder.dividerBottom.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return gradeList.size();
    }

    public static class GradeViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName, tvGrade, tvCredits;
        View dividerBottom;

        public GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            tvGrade = itemView.findViewById(R.id.tvGrade);
            tvCredits = itemView.findViewById(R.id.tvCredits);
            dividerBottom = itemView.findViewById(R.id.dividerBottom);
        }
    }
}
