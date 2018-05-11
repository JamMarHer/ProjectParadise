package paradise.ccclxix.projectparadise.Chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import paradise.ccclxix.projectparadise.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> messageList;
    public String currentUsername;
    private boolean personalPrev = false;
    private boolean otherPrev = false;

    public MessageAdapter(List<Messages> messageList, String currentUsername){
        this.messageList = messageList;
        this.currentUsername = currentUsername;
    }

    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);

        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageAdapter.MessageViewHolder holder, int position) {
        Messages messages = messageList.get(position);
        String from  = messages.getFrom();
        String type = messages.getType();

        if(from.equals(currentUsername)){
            holder.relativeLayout_other.setVisibility(View.INVISIBLE);
            if (position-1 >=0 && from.equals(messageList.get(position-1).getFrom())){
                holder.thumbnail_personal.setVisibility(View.INVISIBLE);
            }else {
                holder.thumbnail_personal.setVisibility(View.VISIBLE);
            }
            if (type.equals("text")){
                holder.messageText_personal.setText(messages.getMessage());
                holder.messageImage_personal.setVisibility(View.INVISIBLE);
            }else {

                Picasso.with(holder.thumbnail_personal.getContext()).load(messages.getMessage())
                        .placeholder(R.drawable.album_example).into(holder.messageImage_personal);
                holder.messageText_personal.setVisibility(View.INVISIBLE);

            }
        }else {
            holder.relativeLayout_personal.setVisibility(View.INVISIBLE);

            if (position-1 >=0 && from.equals(messageList.get(position-1).getFrom())){
                holder.thumbnail_other.setVisibility(View.INVISIBLE);
            }else {
                holder.thumbnail_other.setVisibility(View.VISIBLE);
            }
            if (type.equals("text")){
                holder.messageText_other.setText(messages.getMessage());
                holder.messageImage_other.setVisibility(View.INVISIBLE);
            }else {

                Picasso.with(holder.thumbnail_other.getContext()).load(messages.getMessage())
                        .placeholder(R.drawable.album_example).into(holder.messageImage_other);
                holder.messageText_other.setVisibility(View.INVISIBLE);
            }
        }
       }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends  RecyclerView.ViewHolder{

        public TextView messageText_other;
        public CircleImageView thumbnail_other;
        public ImageView messageImage_other;
        public RelativeLayout relativeLayout_other;

        public TextView messageText_personal;
        public CircleImageView thumbnail_personal;
        public ImageView messageImage_personal;
        public RelativeLayout relativeLayout_personal;

        public MessageViewHolder(View itemView) {
            super(itemView);
            setIsRecyclable(false);
            relativeLayout_other = itemView.findViewById(R.id.message_layout_other);
            messageText_other = itemView.findViewById(R.id.user_message_other);
            thumbnail_other = itemView.findViewById(R.id.user_thump_nail_other);
            messageImage_other = itemView.findViewById(R.id.chat_image_other);

            relativeLayout_personal = itemView.findViewById(R.id.message_layout_personal);
            messageText_personal = itemView.findViewById(R.id.user_message_personal);
            thumbnail_personal = itemView.findViewById(R.id.user_thump_nail_personal);
            messageImage_personal = itemView.findViewById(R.id.chat_image_personal);
        }
    }
}
