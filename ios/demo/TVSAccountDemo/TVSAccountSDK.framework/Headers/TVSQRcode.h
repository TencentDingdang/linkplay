//
//  TVSQRcode.h
//  TVSAccountSDK
//
//  Created by Rinc Liu on 30/8/2018.
//  Copyright © 2018 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TVSAccount.h"

/*!
 * @brief TVS 扫码业务类型
 */
typedef NS_ENUM(NSInteger,TVSQRCodeBusinessType) {
    /*!
     * @brief 设备绑定
     */
    TVSQRCodeBusinessTypeDeviceBind,
    /*!
     * @brief 加入设备亲友圈
     */
    TVSQRCodeBusinessTypeJoinDeviceRelationship
};

/*!
 * @brief TVS 二维码状态类型
 */
typedef NS_ENUM(NSInteger,TVSQRCodeStatus) {
    /*!
     * @brief 未扫描
     */
    TVSQRCodeStatusInit,
    /*!
     * @brief 已扫描
     */
    TVSQRCodeStatusScanned,
    /*!
     * @brief 扫描后点确认
     */
    TVSQRCodeStatusScannedConfirmed,
    /*!
     * @brief 扫描后点取消
     */
    TVSQRCodeStatusScannedCanceled
};

/*!
 * @class TVSQRCodeInfo
 * @brief TVS 二维码相关接口
 */
@interface TVSQRCodeInfo : NSObject

/*!
 * @brief 二维码状态
 */
@property(nonatomic,assign) TVSQRCodeStatus status;

/*!
 * @brief 账号信息
 */
@property(nonatomic,strong) TVSAccountInfo* accountInfo;

/*!
 * @brief 用户信息
 */
@property(nonatomic,strong) TVSUserInfo* userInfo;

@end

/*!
 * @class TVSQRCode
 * @brief TVS 二维码相关接口
 */
@interface TVSQRcode : NSObject

/*!
 * @brief 实例化
 * @param businessType 扫码业务类型
 * @param productId 设备productId
 * @param dsn 设备序列号
 * @return 实例
 */
-(instancetype)initWithBusinessType:(TVSQRCodeBusinessType)businessType productId:(NSString*)productId DSN:(NSString*)dsn;

/*!
 * @brief 获取二维码状态
 * @param handler 回调
 */
-(void)getQRCodeInfoWithHandler:(void(^)(TVSQRCodeInfo*))handler;

/*!
 * @brief 设置二维码状态
 * @param status 二维码状态
 * @param handler 回调，bool表示是否设置成功
 */
-(void)setQRCodeStatus:(TVSQRCodeStatus)status handler:(void(^)(BOOL))handler;

/*!
 * @brief 通过二维码扫描结果，查询相关信息
 * @warning 如果是亲友圈业务，必须确保已登录
 * @param result 二维码扫描结果
 * @param handler 回调，NSDictionary* 参数为后台返回的信息
 */
+(void)queryQRScanResult:(NSString*)result handler:(void(^)(NSDictionary*))handler;

@end
