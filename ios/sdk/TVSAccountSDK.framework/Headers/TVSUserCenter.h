//
//  TVSUserCenter.h
//  TVSAccountSDK
//
//  Created by Rinc Liu on 20/10/2017.
//  Copyright © 2017 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "TVSGeoLocation.h"

/*!
 * @protocol TVSUserCenterDelegate
 * @brief  个人中心回调协议
 */
@protocol TVSUserCenterDelegate <NSObject>

@optional
/*!
 * @brief 获取验证码回调
 * @param success 是否发送成功
 */
-(void)TVSUserCenterResponseGetCaptcha:(BOOL)success;

@optional
/*!
 * @brief 绑定手机号回调
 * @param success 是否绑定成功
 */
-(void)TVSUserCenterResponseBindPhoneNumber:(BOOL)success;

@optional
/*!
 * @brief 绑定地址回调
 * @param success 是否绑定成功
 */
-(void)TVSUserCenterResponseBindLocation:(BOOL)success;

@optional
/*!
 * @brief 查询地址回调
 * @param homeLocations 家庭地址列表
 * @param companyLocations 公司地址列表
 */
-(void)TVSUserCenterResponseHomeLocation:(NSArray<TVSGeoLocation*>*)homeLocations CompanyLocation:(NSArray<TVSGeoLocation*>*)companyLocations;

@end

/*!
 * @class TVSUserCenter
 * @brief TVS用户中心 API
 */
@interface TVSUserCenter : NSObject

/*!
 * @brief 个人中心相关请求回调协议
 */
@property (nonatomic,weak) id<TVSUserCenterDelegate> delegate;

/*!
 * @brief 获得 TVS用户中心帮助类单例对象
 * @return TVS用户中心帮助类实例
 */
+(instancetype)shared;

/*!
 * @brief 前往用户页面
 * @param viewController 起始ViewController
 */
-(void)enterFromViewController:(UIViewController*)viewController;

/*!
 * @brief 获取用于绑定手机号的验证码
 * @param phoneNumber 手机号
 */
-(void)getCaptchaWithPhoneNumber:(NSString*)phoneNumber;

/*!
 * @brief 绑定手机号
 * @param phoneNumber 手机号
 * @param captcha 验证码
 */
-(void)bindPhoneNumber:(NSString*)phoneNumber Captcha:(NSString*)captcha;

/*!
 * @brief 绑定地址
 * @param homeLoc 家庭地址
 * @param companyLoc 公司地址
 */
-(void)bindHomeLocation:(TVSGeoLocation*)homeLoc CompanyLocation:(TVSGeoLocation*)companyLoc;

/*!
 * @brief 查询地址
 */
-(void)queryLocation;

@end
