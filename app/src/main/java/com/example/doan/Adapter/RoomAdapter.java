package com.example.doan.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.doan.R;
import com.example.doan.model.Room;

import java.util.ArrayList;

public class RoomAdapter extends ArrayAdapter<Room> {
    private Context context;
    private ArrayList<Room> rooms;

    public RoomAdapter(Context context, ArrayList<Room> rooms) {
        super(context, 0, rooms);
        this.context = context;
        this.rooms = rooms;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public Room getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        }

        Room room = rooms.get(position);

        TextView tvRoomNameItem = view.findViewById(R.id.tvRoomName);
        TextView tvRoomTypeItem = view.findViewById(R.id.tvRoomType);
        TextView tvRoomIdItem = view.findViewById(R.id.tvRoomId);

        tvRoomNameItem.setText(room.getName());
        tvRoomTypeItem.setText(room.getType());
        tvRoomIdItem.setText("ID: " + room.getId());

        return view;
    }
}
