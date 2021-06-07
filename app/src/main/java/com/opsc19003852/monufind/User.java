package com.opsc19003852.monufind;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class User {

    private DatabaseReference mDatabase;
// ...

    public String username;
    public String email;
    public String units;
    public String landmark;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String units, String landmark) {

        this.username = username;
        this.email = email;
        this.units = units;
        this.landmark= landmark;
    }
    public void writeNewUser(String userId, String name, String email, String units, String landmark) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        User user = new User(name, email, units, landmark);

        mDatabase.child("users").child(userId).setValue(user);

    }
}