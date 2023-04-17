package com.example.thechoiceisyours;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.android_viewer.util.DefaultFragment;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StoryProgression extends FragmentActivity implements ViewerListener {
    private static final int CONTENT_VIEW_ID = 10101010;
    private DefaultFragment fragment;
    private Graph graph;
    public static int nodeCount = 0;
    protected boolean loop = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve Story Asset directory using the Extra put in BookCover.kt
        String bookAssets = getIntent().getStringExtra("assetsFolder");
        String assetsDirectory = bookAssets + "_files/";

        // Concatenate name for directory and file names to be used
        String storyTree = bookAssets + "Tree.txt";
        String storyNodes = bookAssets + "NodeNames.txt";
        String nodesVisitedDB = bookAssets + "NodesVisited";

        // Set up View for GraphStream
        FrameLayout frame = new FrameLayout(this);
        frame.setId(CONTENT_VIEW_ID);
        setContentView(frame, new FrameLayout.LayoutParams( // For GraphStream View
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        // Add a button to return to previous screen
        Button button = new Button(this);

        // Set button styling
        button.setBackgroundResource(R.drawable.rounded_button);
        button.setPadding(25, 25, 25, 25);
        button.setText(getText(R.string.return_btn));
        button.setTextColor(getResources().getColor(R.color.white));
        Typeface typeface = Typeface.create("serif", Typeface.NORMAL);
        button.setTypeface(typeface);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams( // For Button
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.setMargins(50, 50, 50, 50);

        // Add button to the view
        frame.addView(button, params);
        // Button functionality
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent storyProgressionToBookCoverIntent = new Intent(getApplicationContext(), BookCover.class);
                storyProgressionToBookCoverIntent.putExtra("assetsFolder", bookAssets);
                startActivity(storyProgressionToBookCoverIntent);
            }
        });


        // Generate Graph
        graph = new SingleGraph("StoryGraph");
        graph.setAttribute("ui.antialias");

        try {
            // Load the story tree from the assets directory
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open(assetsDirectory + storyTree);

            // Read the text file to create the graph
            Scanner fileScanner = new Scanner(inputStream);
            int numNodes = Integer.parseInt(fileScanner.nextLine());
            addVertices(numNodes, assetsDirectory + storyNodes);  // See method below

            while (fileScanner.hasNextLine()) {
                String vertices = fileScanner.nextLine();
                if (vertices.equals(""))
                    continue;   // In case the vertex does not have any edges
                String[] connection = vertices.split(" ");
                String vertex1 = connection[0];
                String vertex2 = connection[1];
                connectVertices(vertex1, vertex2);  // See method below
            }
            fileScanner.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(graph.edges());
        display(savedInstanceState, graph, true, nodesVisitedDB);
    }

    // Refer to: https://github.com/caturananta/AndroidGraphView
    // From post: https://stackoverflow.com/questions/66806508/graphstream-android-not-able-to-display-graph-in-fragment
    /** In onStart, the AndroidViewer is already created */
    @Override
    protected void onStart() {
        super.onStart();

        ViewerPipe pipe = fragment.getViewer().newViewerPipe();
        pipe.addViewerListener(this);

        String styleSheet = "graph {" +
                "fill-mode: gradient-radial;" +
                "fill-color: white, rgba(68,114,196,100);" +
                "padding: 60px;" +
                "}" +

                "node {" +
                "shape: circle;" +
                "size: 48,48;" +
                "fill-mode: gradient-vertical;" +
                "fill-color: gray;" +
                "stroke-mode: plain;" +
                "stroke-color: gray;" +
                "stroke-width: 9px;" +
                "shadow-mode: plain;" +
                "shadow-width: 0px;" +
                "shadow-offset: 15px, -15px;" +
                "shadow-color: rgba(0,0,0,100);" +
                "text-size: 20px;" +
                "}" +

                "node.green {" +
                "fill-color: green;" +
                " }" +

                "node:clicked {" +
                "stroke-mode: plain;" +
                "stroke-color: red;" +
                "}" +

                "node:selected {" +
                "stroke-mode: plain;" +
                "stroke-width: 4px;" +
                "stroke-color: blue;" +
                "}" +

                "edge {" +
                "size: 5px;" +
                "arrow-size: 20px, 15px;" +
                "shape: cubic-curve;" +
                "fill-color: rgb(128,128,128);" +
                "fill-mode: plain;" +
                "stroke-mode: plain;" +
                "stroke-color: rgb(80,80,80);" +
                "stroke-width: 9px;" +
                "shadow-mode: none;" +
                "shadow-color: rgba(0,0,0,50);" +
                "shadow-offset: 15px, -15px;" +
                "shadow-width: 0px;" +
                "arrow-shape: diamond;" +
                "}" +

                "edge.green {" +
                "fill-color: green;" +
                " }";

        graph.setAttribute("ui.stylesheet", styleSheet);

        new Thread( () ->  {
            while (loop) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pipe.pump();
            }

            System.exit(0);
        }).start();
    }
    /* End of https://github.com/caturananta/AndroidGraphView */

    public void addVertices(int numberNodes, String nodeFile) throws IOException {
        // For Vertex/Node label, determine the Node ID from a NodeNames.txt asset
        AssetManager assetManager = getAssets();
        InputStream inputStream = assetManager.open(nodeFile);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        // Retain the node IDs in an array
        String[] nodeNames = new String[numberNodes];
        String line;
        for (int i = 0; i < numberNodes; i++) {
            graph.addNode(String.valueOf(i));
            line = bufferedReader.readLine();
            nodeNames[i] = line;
        }

        // Set the ID label attribute for the nodes from the ID array
        int name = 0;
        for (Node n : graph) {
            n.setAttribute("ui.label", String.valueOf(nodeNames[name]));
            name++;
            nodeCount++;
        }
    }

    public void connectVertices(String vertex1, String vertex2) {
        String edgeID = vertex1 + vertex2;
        graph.addEdge(edgeID, vertex1, vertex2);
    }

    // Refer to GraphStream's GitHub ReadMe:
    /** https://github.com/graphstream/gs-ui-android */
    public void display(Bundle savedInstanceState, Graph graph, boolean autoLayout, String nodesVisitedDB) {

        // Retrieve Visited Nodes from the Firebase database
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users/" + userID + "/" + nodesVisitedDB);

        // Iterate through Visited Node values from database, and add "true" chapters to List
        List<String> visitedTrue = new ArrayList<>();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    // Get the value of the boolean for each chapter
                    boolean value = Boolean.TRUE.equals(itemSnapshot.getValue(Boolean.class));
                    if (value) {  // Add visited "true" to List
                        String data = itemSnapshot.getKey();
                        visitedTrue.add(data);
                        Log.d("SUCCESS", "Value of " + itemSnapshot.getKey() + " is " + true);
                    }
                }
                // Toggle the color of visited nodes to green
                for (String visitedNode : visitedTrue) {
                    for (Node node : graph) {
                        if (node.getAttribute("ui.label").equals(visitedNode)) {
                            node.setAttribute("ui.class", "green");
                        }
                    }
                }
                // Toggle the color of edges between visited nodes to green
                for (int i = 1; i < visitedTrue.size(); i++) {
                    System.out.println(visitedTrue.get(i - 1) + visitedTrue.get(i));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
                Log.e("FAIL", "Error retrieving data", error.toException());
            }

        });

        // Display Graph
        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();

            // find fragment or create him
            fragment = (DefaultFragment) fm.findFragmentByTag("fragment_tag");
            if (null == fragment) {
                fragment = new DefaultFragment();
                fragment.init(graph, autoLayout);
            }

            // Add the fragment in the layout and commit
            FragmentTransaction ft = fm.beginTransaction() ;
            ft.add(CONTENT_VIEW_ID, fragment).commit();
        }
    }
    /* End of https://github.com/graphstream/gs-ui-android */

    public void buttonPushed(String id) { }

    public void buttonReleased(String id) { }

    public void mouseOver(String id) { }

    public void mouseLeft(String id) { }

    public void viewClosed(String viewName) {
        loop = false;
    }

}