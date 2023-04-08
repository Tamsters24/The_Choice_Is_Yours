package com.example.thechoiceisyours;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.blox.treeview.BaseTreeAdapter;
import de.blox.treeview.TreeNode;
import de.blox.treeview.TreeView;
import org.jetbrains.annotations.NotNull;

public class StoryProgression extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_progression);
        TreeView treeView = findViewById(R.id.treeview);
        BaseTreeAdapter<ViewHolder> adapter = new BaseTreeAdapter<ViewHolder>(this, R.layout.node) {

            @NonNull
            @NotNull
            @Override
            public ViewHolder onCreateViewHolder(View view) {
                return new  ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ViewHolder viewHolder, Object data, int position) {
                viewHolder.textView.setText(data.toString());
            }
        };
        treeView.setAdapter(adapter);
        TreeNode rootNode = new TreeNode("Part 1");
        TreeNode child1 = new TreeNode("Child 1");
        TreeNode child2 = new TreeNode("Child 2");
        TreeNode child3 = new TreeNode("Child 3");
        TreeNode child4 = new TreeNode("Child 4");

        child2.addChild(child3);
        child2.addChild(child4);
        rootNode.addChild(child1);
        rootNode.addChild(child2);
        adapter.setRootNode(rootNode);
    }
}
