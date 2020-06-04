//
//  SmartQSession.swift
//  kopo_ios
//
//  Created by Dayyoung on 2020/06/02.
//  Copyright © 2020 Dayyoung. All rights reserved.
//

import Foundation
class SmartQSession{
    
    //private static SmartQSession smartQSession;
    public static var TAG = "SmartQSession";
    public static var PREFIX = "HYCU";
    public static var SMARTQID = "smartqid";
   // public static var context = nil;
   //var smartQUserForSet = nil;
    public static var currentSmartQUser:SmartQUser? = nil
    //public static var currentSmartQUser;
    public static var smartQUserList:[SmartQUser] = [];
    
    //Public
    public static func getUserData(_ smartQID:Int) -> UserDefaults {
        let smartQIDString = String(smartQID)
        let defaults = UserDefaults(suiteName: PREFIX + smartQIDString)
        return defaults!
    }
    
    //Private
    private static func setSmartQID(_ smartQID:Int) {
        if let defaults = UserDefaults(suiteName: PREFIX + SMARTQID) {
            defaults.set(smartQID,  forKey: SMARTQID)
        }
    }
    
    //Private
    private static func getSmartQID() -> Int{
        var smartQID = 0
        if let defaults = UserDefaults(suiteName: PREFIX + SMARTQID) {
            smartQID = defaults.integer(forKey: SMARTQID)
        }
        return smartQID
    }
    
    public static func loadAllSmartQUser() {
        
        let maxSmartQID = getSmartQID()

        smartQUserList = []

        for i in 0...maxSmartQID {
            print("\(i) times")
            
            let smartQUser = loadSmartQUser(i+1);
            if smartQUser.id != 0 {
                smartQUserList.append(smartQUser)
            }
        }
    }
    
    
    //public
    public static func setSmartQUser(_ smartQUser : SmartQUser) {

        let maxSmartQID = getSmartQID()
        
        var smartQUserForSet:SmartQUser? = nil
        
        for i in 0...maxSmartQID {
  
            var nowSmartQUser = loadSmartQUser(i+1)

            if nowSmartQUser.id != 0 {
                if nowSmartQUser.userId == smartQUser.userId {
                    smartQUserForSet = smartQUser
                    smartQUserForSet!.id = nowSmartQUser.id
                }
            }
        }

        if (smartQUserForSet != nil) {
            //update
            let smartQID = updateSmartQUser(smartQUserForSet!); //save file
            smartQUserForSet = smartQUser
            smartQUserForSet!.id = smartQID

        }else {
            let smartQID = insertSmartQUser(smartQUser); //save file
            smartQUserForSet = smartQUser
            smartQUserForSet!.id = smartQID
        }
        
        self.currentSmartQUser = smartQUserForSet! // save now user
        loadAllSmartQUser(); //refresh
    }
    
    public static func getSmartQAllUserToString() -> String {

        let encoder = JSONEncoder()
        let jsonData = try? encoder.encode(smartQUserList)
        
        let jsonString = String(data: jsonData!, encoding: .utf8)
        
        return jsonString!;
    }
    
    private static func insertSmartQUser(_ smartQUser : SmartQUser ) -> Int{

        var smartQID = getSmartQID()
        smartQID += 1 //Incremental +1
        setSmartQID(smartQID)
        
        let smartQIDString = String(smartQID)

        if let defaults = UserDefaults(suiteName: PREFIX + smartQIDString) {
            defaults.set(smartQID,  forKey: SmartQUser.ID)
            defaults.set(smartQUser.userId,  forKey: SmartQUser.USERID)
            defaults.set(smartQUser.password,  forKey: SmartQUser.PASSWORD)
        }
        return smartQID
    }
    
    private static func updateSmartQUser(_ smartQUser : SmartQUser ) -> Int{

        let smartQID = smartQUser.id
        
        let smartQIDString = String(smartQID)

        if let defaults = UserDefaults(suiteName: PREFIX + smartQIDString) {
            defaults.set(smartQUser.id,  forKey: SmartQUser.ID)
            defaults.set(smartQUser.userId,  forKey: SmartQUser.USERID)
            defaults.set(smartQUser.password,  forKey: SmartQUser.PASSWORD)
        }
        return smartQID
    }
    
    private static func deleteSmartQUser(_ smartQID : Int ) {

        let smartQIDString = String(smartQID)
        
        if let defaults = UserDefaults(suiteName: PREFIX + smartQIDString) {
            defaults.removeObject(forKey: SmartQUser.ID)
            defaults.removeObject(forKey: SmartQUser.USERID)
            defaults.removeObject(forKey: SmartQUser.PASSWORD)
        }
        
        if smartQID == currentSmartQUser?.id {
            currentSmartQUser = nil
        }
    }
    
    public static func  deleteAllSmartQUser() {

        let maxSmartQID = getSmartQID();
        
        for i in 0...maxSmartQID {
            //loadSmartQUser()
            let nowSmartQUser = loadSmartQUser(i+1);

            if(nowSmartQUser.id != 0) {
                deleteSmartQUser(nowSmartQUser.id);
            }
        }
        loadAllSmartQUser();
        setSmartQID(0);
    }
    
    public static func  deleteSmartQUserByID(_ smartQUser : SmartQUser ) {

        let maxSmartQID = getSmartQID();
        
        for i in 0...maxSmartQID {
            
            let nowSmartQUser = loadSmartQUser(i+1);

            if(nowSmartQUser.userId == smartQUser.userId) {
                deleteSmartQUser(nowSmartQUser.id);
            }
        }
        loadAllSmartQUser();
    }
    
    private static func loadSmartQUser(_ smartQID : Int ) -> SmartQUser{

        //let smartQUser = nil
        var smartQUser:SmartQUser = SmartQUser()
               
        let smartQIDString = String(smartQID)

        if let defaults = UserDefaults(suiteName: PREFIX + smartQIDString) {
            
            let isExist = defaults.integer(forKey: SmartQUser.ID)
            
            if(isExist > 0){
                smartQUser.id = defaults.integer(forKey: SmartQUser.ID)
                smartQUser.userId = defaults.string(forKey: SmartQUser.USERID)
                smartQUser.password = defaults.string(forKey: SmartQUser.PASSWORD)
            }
        }
        return smartQUser
    }
    
    struct SmartQUser : Codable {
        static let ID = "id";
        static let USERID = "userId";
        static let PASSWORD = "password";
        var id:Int = 0;
        var userId:String? = nil;
        var password:String? = nil;
    }
    
}
