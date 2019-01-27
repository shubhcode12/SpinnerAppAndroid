

package sol.earningapp.smtech.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import java.util.List;

import sol.earningapp.smtech.R;
import sol.earningapp.smtech.models.Withdraw;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.FriendListViewholder> {
    Context mContext;
    List<Withdraw> withdrawList;
    UserClickedListener userClickedListener;

    public UserListAdapter(Context mContext, List<Withdraw> withdrawList, UserClickedListener userClickedListener) {
        this.withdrawList = withdrawList;
        this.mContext = mContext;
        this.userClickedListener = userClickedListener;
    }

    @Override
    public UserListAdapter.FriendListViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = parent.inflate(mContext, R.layout.user_list_row, null);
        FriendListViewholder friendListViewholder = new FriendListViewholder(view);
        return friendListViewholder;
    }

    @Override
    public void onBindViewHolder(UserListAdapter.FriendListViewholder holder, final int position) {

        holder.tvName.setText(withdrawList.get(position).getWithdrawDate());
        holder.tvDescription.setText(withdrawList.get(position).getWithdrawPoints() + " (Pending)");
//        holder.tvDescription.setText(withdrawList.get(position).getDescription());
        holder.btnUserDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userClickedListener.onUserClicked(withdrawList.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return withdrawList.size() > 0 ? withdrawList.size() : 0;
    }

    public void updateData(List<Withdraw> withdrawList) {
        this.withdrawList.clear();
        this.withdrawList.addAll(withdrawList);
        this.notifyDataSetChanged();
    }

    public class FriendListViewholder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDescription;
        private Button btnUserDetails;

        public FriendListViewholder(View itemView) {
            super(itemView);
            this.tvName = (TextView) itemView.findViewById(R.id.tvName);
            this.tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            this.btnUserDetails = (Button) itemView.findViewById(R.id.btnUserDetails);
        }
    }

    public interface UserClickedListener {
        void onUserClicked(Withdraw user);
    }
}
