//
//  TVSDeviceRelation.h
//  TVSAccountSDK
//
//  Created by Rinc Liu on 30/8/2018.
//  Copyright © 2018 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TVSAccount.h"



/*!
 * @brief TVS 设备亲友圈好友添加类型
 */
typedef NS_ENUM(NSInteger,TVSDeviceRelationAddType) {
    /*!
     * @brief 添加管理员
     */
    TVSDeviceRelationAddTypeAdmin,
    /*!
     * @brief 添加普通用户
     */
    TVSDeviceRelationAddTypeUser
};



/*!
 * @brief TVS 设备亲友圈好友删除类型
 */
typedef NS_ENUM(NSInteger,TVSDeviceRelationDeleteType) {
    /*!
     * @brief 普通用户退出
     */
    TVSDeviceRelationDeleteTypeUserExit,
    /*!
     * @brief 管理员删除用户
     */
    TVSDeviceRelationDeleteTypeAdminDeleteUser,
    /*!
     * @brief 管理员解散亲友圈
     */
    TVSDeviceRelationDeleteTypeAdminDeleteAll
};



/*!
 * @brief TVS 设备亲友圈好友查询类型
 */
typedef NS_ENUM(NSInteger,TVSDeviceRelationQueryType) {
    /*!
     * @brief 根据设备查询
     */
    TVSDeviceRelationQueryTypeDevice,
    /*!
     * @brief 根据账号查询
     */
    TVSDeviceRelationQueryTypeAccount
};



/*!
 * @class TVSDeviceRelationDeviceInfo
 * @brief TVS 设备亲友圈设备信息
 */
@interface TVSDeviceRelationDeviceInfo : NSObject

/*!
 * @brief 设备productId
 */
@property(nonatomic,copy) NSString* productId;

/*!
 * @brief 设备dsn
 */
@property(nonatomic,copy) NSString* dsn;

/*!
 * @brief 设备昵称
 */
@property(nonatomic,copy) NSString* nickName;

@end



/*!
 * @class TVSDeviceRelationAccountInfo
 * @brief TVS 设备亲友圈用户账号信息
 */
@interface TVSDeviceRelationAccountInfo : NSObject

/*!
 * @brief 账号类型
 */
@property(nonatomic,assign) TVSAccountType accountType;

/*!
 * @brief 好友添加类型
 */
@property(nonatomic,assign) TVSDeviceRelationAddType addType;

/*!
 * @brief 账号openId
 */
@property(nonatomic,copy) NSString* openId;

/*!
 * @brief 账号appid
 */
@property(nonatomic,copy) NSString* appId;

/*!
 * @brief 昵称
 */
@property(nonatomic,copy) NSString* nickName;

/*!
 * @brief 头像
 */
@property(nonatomic,copy) NSString* headImageUrl;

/*!
 * @brief 权限信息
 */
@property(nonatomic,copy) NSDictionary* permissionInfo;

/*!
 * @brief 备注
 */
@property(nonatomic,copy) NSString* remark;

@end



/*!
 * @class TVSDeviceRelationBaseInfo
 * @brief TVS 设备亲友圈基本信息（设备和管理员）
 */
@interface TVSDeviceRelationBaseInfo : NSObject

/*!
 * @brief 设备信息
 */
@property(nonatomic,strong) TVSDeviceRelationDeviceInfo* deviceInfo;

/*!
 * @brief 管理员账户信息
 */
@property(nonatomic,strong) TVSDeviceRelationAccountInfo* adminAccount;

@end



/*!
 * @class TVSDeviceRelationDetailInfo
 * @brief TVS 设备亲友圈信息（基本信息和成员列表）
 */
@interface TVSDeviceRelationDetailInfo : NSObject

/*!
 * @brief 基本信息
 */
@property(nonatomic,strong) TVSDeviceRelationBaseInfo* baseInfo;

/*!
 * @brief 成员列表
 */
@property(nonatomic,copy) NSArray<TVSDeviceRelationAccountInfo*>* accountList;

@end



/*!
 * @class TVSDeviceRelation
 * @brief TVS 设备亲友圈业务
 */
@interface TVSDeviceRelation : NSObject

/*!
 * @brief 实例化
 * @return 实例
 */
+(instancetype)shared;

/*!
 * @brief 修改用户（的备注）
 * @param scanResult 亲友圈二维码扫描结果
 * @param handler 回调，TVSDeviceRelationBaseInfo 参数为亲友圈设备和管理员信息
 */
-(void)queryDeviceRelationBaseInfoByQRCodeScanResult:(NSString*)scanResult handler:(void(^)(TVSDeviceRelationBaseInfo*))handler;

/*!
 * @brief 添加用户
 * @param type 添加类型
 * @param baseInfo 亲友圈基本信息（其中设备信息和管理员信息都需要传！）
 * @param accountInfo 待添加的用户账号信息（其中 accounttype、accountid、accountappid 三个字段必填！）
 * @param handler 回调，bool 参数表示是否成功
 */
-(void)addRelationWithType:(TVSDeviceRelationAddType)type baseInfo:(TVSDeviceRelationBaseInfo*)baseInfo accountInfo:(TVSDeviceRelationAccountInfo*)accountInfo handler:(void(^)(BOOL))handler;

/*!
 * @brief 删除用户
 * @param type 删除类型
 * @param baseInfo 亲友圈基本信息（其中设备信息和管理员信息都需要传！）
 * @param accountInfo 待删除的用户账号信息（其中 accounttype、accountid、accountappid 三个字段必填！）
 * @param handler 回调，bool 参数表示是否成功
 */
-(void)deleteRelationWithType:(TVSDeviceRelationDeleteType)type baseInfo:(TVSDeviceRelationBaseInfo*)baseInfo accountInfo:(TVSDeviceRelationAccountInfo*)accountInfo handler:(void(^)(BOOL))handler;

/*!
 * @brief 修改用户（的备注）
 * @param baseInfo 亲友圈基本信息（其中设备信息和管理员信息都需要传！）
 * @param accountInfo 待修改的用户账号信息（其中 accounttype、accountid、accountappid、remark 四个字段必填！）
 * @param handler 回调，bool 参数表示是否成功
 */
-(void)updateRelationWithBaseInfo:(TVSDeviceRelationBaseInfo*)baseInfo accountInfo:(TVSDeviceRelationAccountInfo*)accountInfo handler:(void(^)(BOOL))handler;

/*!
 * @brief 查询用户
 * @param type 查询类型
 * @param deviceInfo 设备信息（按设备查询时需要传此参数，其中 productid、dsn 两个字段必填！）
 * @param accountInfo 待查询的用户账号信息（按用户查询时需要传此参数，其中 accounttype、accountid、accountappid 三个字段必填！）
 * @param handler 回调，参数为设备亲友圈关系列表
 */
-(void)queryRelationWithType:(TVSDeviceRelationQueryType)type deviceInfo:(TVSDeviceRelationDeviceInfo*)deviceInfo accountInfo:(TVSDeviceRelationAccountInfo*)accountInfo handler:(void(^)(NSArray<TVSDeviceRelationDetailInfo*>*))handler;

@end
