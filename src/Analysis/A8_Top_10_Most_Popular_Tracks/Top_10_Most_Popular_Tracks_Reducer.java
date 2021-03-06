/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Analysis.A8_Top_10_Most_Popular_Tracks;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Chintan
 */
public class Top_10_Most_Popular_Tracks_Reducer extends Reducer<Text, IntWritable, NullWritable, Text> {
    private TreeMap<Integer, String> top10 = new TreeMap<Integer, String>(Collections.reverseOrder());

    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int totalUniquePlayCount = 0;

        // get count and add
        for (IntWritable uniqueCount : values) {
            totalUniquePlayCount += uniqueCount.get();
        }

        //add this track with its play count to tree map
        top10.put(totalUniquePlayCount, key.toString());

        // if map size has grown > 10 then remove first entry as tree map sorts in ascending order
        if(top10.size() > 10){
            top10.remove(top10.lastKey());
        }
    }


    // Will be called once all keys are parsed
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        for(Map.Entry<Integer, String> entry : top10.entrySet()) {

            //Integer key = entry.getKey();
            String value = entry.getValue().substring(0, 1).toUpperCase() + entry.getValue().substring(1);

            // print top 10 tracks
            context.write(NullWritable.get(), new Text(value));
        }
    }
}

