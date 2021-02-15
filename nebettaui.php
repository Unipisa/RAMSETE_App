<?php
//I just need wp once
require_once('wp-load.php');

//variables to specify in the URL
//operation requested
$select_op = $_REQUEST["op"];
//user name
$select_name = $_REQUEST["name"];
//encrypted password
$select_pass = $_REQUEST["pass"];
//user email
$select_email = $_REQUEST["email"];
//QR ID
$select_QR = $_REQUEST["QR"];
//animal type
$select_animal = $_REQUEST["animal"];
//animal type's obtained points
$select_points = $_REQUEST["points"];

//checking operation type
switch ($select_op){
    //login username
    //requires op, name
    //returns a string (the encoded pass)
    case "login":
        //query injected selects just the password
        $query = " SELECT pass FROM museum_users WHERE name='".$select_name."'";
        //results found
        $get = $wpdb->get_results($query);
        //prints results
        //if no username is found the JSON array will be empty
        echo $get[0]->pass;
        break;

    //register username
    //requires op, name, email, pass
    //returns just a number 0 ok
    //                     -1 error
    case "register":
        //insert table entry
        $result = $wpdb->insert("museum_users", array(
                                                    'name' => $select_name,
                                                    'pass' => $select_pass,
                                                    'email' => $select_email));
        //check if username is already registered (only Unique value)
        if ($result == 1){
            //value of 1 returned if the entry was added
            echo 0;
        }else{
            //no value if no entry added
            echo -1;
        }
        break;

    //updates the points of the username
    //adding points to a QR code ID
    //already existing in the JsonArray
    //requires  op,
    //            name,
    //            QR,
    //            animal,
    //            points
    //returns just a number 0 points updated
    //                      1 added new animal field with points
    //errors: -1 no QR IDs at all in array
    //        -2 if specific QR not in array
        //    -3 if could not add points to db
    case "updatePoints":
        //first gets the JSONARRAY
        //injected query selects just the progress
        $query = " SELECT progress FROM museum_users WHERE name='".$select_name."'";
        //JosnArray found
        $get = $wpdb->get_results($query);
        //check if JsonArray is null no need to parse
        //null has to be a string or won't be found
        //progress is accessed as an object
        if($get[0]->progress == 'null'|| empty($get)){
            //there's no QR to update
            echo -1;
            break;
        }
        //JsonArray exists!
        //check for QR ID
        //array with json objects
        $json_array_QRID = json_decode($get[0]->progress);


        //check if QR ID exists
        $existentialism = false;
        foreach($json_array_QRID as $id_iterator){
            if($id_iterator->ID == $select_QR)
                $existentialism = true;
        }
        if(!$existentialism){
            echo -2;
            break;
        }

        //iterator for QRIDs
        $id_iterator = "not";
        //iterator for array index
        $it = 0;
        $max = count($json_array_QRID);
        $correct_index = $it;
        //searches the specified QRID
        while($id_iterator != $select_QR && $it < $max){
            $correct_index = $it;
            $id_iterator = $json_array_QRID[$it]->ID;
            $it ++;
        }

        //saves the QRID jsonObject found
        $found = $json_array_QRID[$correct_index];
        //check if the animal type's points already exist
        if(property_exists($found,$select_animal)){
            //adds points to existing property
            $found->$select_animal += $select_points;
            //esiste ed ho aggiunto i punti
            $result = 0;
        }else{
           //property not present
           //transforms object into array->adds property->change back to object
            $found = (array)$found;
            $found[$select_animal] = $select_points;
            $found = (object)$found;
            //non esiste e ho messo un nuovo campo
            $result = 1;
        }
        //puts modified object back into array
        $json_array_QRID[$correct_index] = $found;
        //encodes the array
        $put = json_encode($json_array_QRID);

        //puts array into database
        $aaa = $wpdb->update(
                'museum_users',
                array('progress'=>$put),
                array('name'=>$select_name)
            );
        //always remember for an array use "=" for accessing object fields use "-"
        if($aaa==1){
            //entry added
            echo $result;
        }else{
            //entry not added
            echo -3;
        }


        break;

    //adds a new QRID to the list of QRIDs found by the user
    //requires select_QR, select_op, select_name
    //returns just a number 0 ok
    //                      1 first QR added
    //                      2 QR already added
    //erreors: -1 update gone wrong
    case "addQR":
        //first gets the JSONARRAY
        //injected query selects just the progress
        $query = " SELECT progress FROM museum_users WHERE name='".$select_name."'";
        //JosnArray found
        $get = $wpdb->get_results($query);
        //check if JsonArray is null no need to parse
        //null has to be a string or won't be found
        //progress is accessed as an object
        if($get[0]->progress == 'null'){
            //adds an array with 1 element
            $wpdb->update(
                    'museum_users',
                    array('progress'=>'[{"ID":"'.$select_QR.'"}]'),
                    array('name'=>$select_name)
                );
            //"First QR added!"
            echo 1;
        }else{
            //creates 1 object with ID field
            $newID = new stdClass();
            $newID->ID = $select_QR;

            //adds object to the array (if it's not already present)
            $json_array_QRID = json_decode($get[0]->progress);

            //iterator for QRIDs
            $id_iterator = "not";
            //iterator for array index
            $it = 0;
            $max = count($json_array_QRID);
            $found = false;
            //searches the specified QRID
            while($id_iterator != $select_QR && $it < $max){
                $id_iterator = $json_array_QRID[$it]->ID;
                //if found change variable
                if($id_iterator == $select_QR)
                    $found = true;
                $it ++;
            }

            //exit case if ID already there
            if ($found){
                //"already there!"
                echo 2;
                break;
            }

            //add to array if not found
            $json_array_QRID[] = $newID;
            //push onto the server
            $result = $wpdb->update(
                'museum_users',
                array('progress'=>json_encode($json_array_QRID)),
                array('name'=>$select_name)
            );
            if ($result == 1){
                //ok
                echo 0;
            }else{
                //update gone wrong
                echo -1;
            }
        }
        break;

    //returns the profile of the user
    //obtained with current points
    //overwrites the old one (if present)
    //requires op, name
    //returns a string with the profile name
    //       if there are QR but no points an empty string is returned
    //errors: null no QR in the array
    //        "sort" problem sorting the array
    case "makeProfile":
        //first gets the JSONARRAY
        //injected query selects just the progress
        $query = " SELECT progress FROM museum_users WHERE name='".$select_name."'";
        //JosnArray found
        $get = $wpdb->get_results($query);
        //check if JsonArray is null no need to parse
        //null has to be a string or won't be found
        //progress is accessed as an object
        if($get[0]->progress == 'null'||empty($get)){
            //no QR or points to analize
            echo null;
            break;
        }
        //JsonArray exists!
        //check for QR ID
        //array with json objects
        $json_array_QRID = json_decode($get[0]->progress);
        //new array with all point types
        $totals = array();
        //add points from every QR ID
        foreach($json_array_QRID as $val_QR){
            //turning object into array for searching/adding points/types
            $val_QR = (array) $val_QR;
            foreach($val_QR as $field_QR => $points){
                //ignore ID field
                if($field_QR == "ID")
                    continue;
                //if field already in array
                if(array_key_exists($field_QR,$totals)){
                    //adds points
                    $totals[$field_QR] += $points;
                }else{
                    //field not in array adds field with current points
                    $totals[$field_QR] = $points;
                }
            }

        }

        //now we have all points
        //sort array elements
        //take first element(max one)
        if(!arsort($totals,SORT_NUMERIC)){
            echo "sort";
            break;
        }

        //put profile into wpdb
        $result = $wpdb->update(
            'museum_users',
            array('profile'=>array_key_first($totals)),
            array('name'=>$select_name)
        );
        //since number of rows updated is returned
        //could be 0 because it's the same string
        if($result >= 0){
            //update pushed returning string of animal type
            echo array_key_first($totals);
        }else{
            //update not pushed returning string error
            echo "update";
        }
        break;

    //returns stored profile of the user as a Json object
    //requires op, name
    //returns string with animal type
    //errors: an empty string is returned 
    //          if there's no user or string in the field
    case "getProfile":
        //query injected selects just the profile
        $query = " SELECT profile FROM museum_users WHERE name='".$select_name."'";
        //results found
        $get = $wpdb->get_results($query);
        //if no username is found the JSON array will be empty
        echo $get[0]->profile;
        break;



}
