package com.example.vidify.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vidify.FireBase.DBClass;
import com.example.vidify.FireBase.DBInterface;
import com.example.vidify.Listeners.UsersListener;
import com.example.vidify.Models.User;
import com.example.vidify.R;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{
    private List<User> users;
    private UsersListener usersListener;
    private List<User> selectedUsers;
    public Context context;

    public UsersAdapter(List<User> users, UsersListener usersListener)
    {
        this.users = users;
        this.usersListener = usersListener;
        selectedUsers = new ArrayList<>();
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setContext(Context context)
    {
        this.context = context;
    }

    class UserViewHolder extends RecyclerView.ViewHolder
    {
        TextView textFirstChar, textUserName, textEmail;
        ImageView imageAudio, imageVideo;
        ConstraintLayout userContainer;
        ImageView imageSelected,imageRemove;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textFirstChar = itemView.findViewById(R.id.textFirstChar);
            textUserName = itemView.findViewById(R.id.textUsername);
            textEmail = itemView.findViewById(R.id.textEmail);

            imageAudio = itemView.findViewById(R.id.imageAudioMeeting);
            imageVideo = itemView.findViewById(R.id.imageVideoMeeting);

            userContainer = itemView.findViewById(R.id.userContainer);
            imageSelected = itemView.findViewById(R.id.imageSelected);

            imageRemove = itemView.findViewById(R.id.remove);
        }
        void setUserData(User user)
        {

            textFirstChar.setText(user.name.substring(0,1));
            textUserName.setText(user.name);
            textEmail.setText(user.email);
            imageAudio.setOnClickListener(v->
            {
                usersListener.initiateAudioMeeting(user);
            });
            imageVideo.setOnClickListener(v->
            {
                usersListener.initiateVideoMeeting(user);
            });

            userContainer.setOnLongClickListener(v->
            {
                if(imageSelected.getVisibility()!=View.VISIBLE)
                {
                    selectedUsers.add(user);
                    imageSelected.setVisibility(View.VISIBLE);
                    imageVideo.setVisibility(View.GONE);
                    imageAudio.setVisibility(View.GONE);
                    imageRemove.setVisibility(View.VISIBLE);
                    usersListener.onMultipleUsersAction(true);
                }
                return  true;
            });

            userContainer.setOnClickListener(v->
            {
                if(imageSelected.getVisibility() == View.VISIBLE)
                {
                    selectedUsers.remove(user);
                    imageSelected.setVisibility(View.GONE);
                    imageVideo.setVisibility(View.VISIBLE);
                    imageAudio.setVisibility(View.VISIBLE);
                    imageRemove.setVisibility(View.GONE);
                    if(selectedUsers.size() == 0)
                    {
                        usersListener.onMultipleUsersAction(false);
                    }
                }
                else
                {
                    if(selectedUsers.size()>0)
                    {
                        selectedUsers.add(user);
                        imageRemove.setVisibility(View.GONE);
                        imageSelected.setVisibility(View.VISIBLE);
                        imageVideo.setVisibility(View.GONE);
                        imageAudio.setVisibility(View.GONE);
                    }
                }

            });
            imageRemove.setOnClickListener(v->
            {
                DBInterface dbInterface = new DBClass(context);
                dbInterface.deleteOneRow(user.name);
                users.remove(user);
                notifyDataSetChanged();
                imageSelected.setVisibility(View.GONE);
                imageVideo.setVisibility(View.VISIBLE);
                imageAudio.setVisibility(View.VISIBLE);
                imageRemove.setVisibility(View.GONE);
                usersListener.onMultipleUsersAction(false);
            });
        }

    }
}
