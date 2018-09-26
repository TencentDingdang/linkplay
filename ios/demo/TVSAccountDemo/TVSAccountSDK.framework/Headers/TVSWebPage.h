//
//  TVSWebPage.h
//  TVSAccountSDK
//
//  Created by Rinc Liu on 19/9/2018.
//  Copyright © 2018 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

/*!
 * @brief TVS 相关 WEB 页面
 */
typedef NS_ENUM(NSInteger,TVSWebPageType) {
    /*!
     * @brief 个人中心页面
     */
    TVSWebPageTypeMember,
    /*!
     * @brief 会员领取页面
     */
    TVSWebPageTypeGetVIP,
    /*!
     * @brief 会员充值页面
     */
    TVSWebPageTypeRecharge,
    /*!
     * @brief 手机号地址页面
     */
    TVSWebPageTypePhoneAddress,
    /*!
     * @brief 用户反馈页面
     */
    TVSWebPageTypeFeedback,
    /*!
     * @brief AI 训练师（自定义问答）
     */
    TVSWebPageTypeAITrainer
};


/*!
 * @protocol TVSWebPageDelegate
 * @brief TVS WEB 页面回调
 */
@protocol TVSWebPageDelegate<NSObject>

@optional
/*!
 * @brief H5页面已加载
 * @param pageType 页面类型
 */
-(void)TVSDidLoadPage:(TVSWebPageType)pageType;

@optional
/*!
 * @brief H5页面即将出现
 * @param pageType 页面类型
 */
-(void)TVSWillAppearPage:(TVSWebPageType)pageType;

@optional
/*!
 * @brief H5页面已经出现
 * @param pageType 页面类型
 */
-(void)TVSDidAppearPage:(TVSWebPageType)pageType;

@optional
/*!
 * @brief H5页面即将消失
 * @param pageType 页面类型
 */
-(void)TVSWillDisappearPage:(TVSWebPageType)pageType;

@optional
/*!
 * @brief H5页面已经消失
 * @param pageType 页面类型
 */
-(void)TVSDidDisappearPage:(TVSWebPageType)pageType;

@optional
/*!
 * @brief H5页面QQ登录
 * @param pageType 页面类型
 * @param result QQ登录结果
 */
-(void)TVSPage:(TVSWebPageType)pageType qqLoginResult:(BOOL)result;

@optional
/*!
 * @brief H5页面微信登录
 * @param pageType 页面类型
 * @param result 微信登录结果
 */
-(void)TVSPage:(TVSWebPageType)pageType wxLoginResult:(BOOL)result;

@optional
/*!
 * @brief H5页面QQ验票
 * @param pageType 页面类型
 * @param result QQ验票结果
 */
-(void)TVSPage:(TVSWebPageType)pageType verifyQQTokenResult:(BOOL)result;

@optional
/*!
 * @brief H5页面微信刷票
 * @param pageType 页面类型
 * @param result 微信刷票结果
 */
-(void)TVSPage:(TVSWebPageType)pageType refreshWXTokenResult:(BOOL)result;

@optional
/*!
 * @brief H5页面微信支付
 * @param pageType 页面类型
 * @param result 微信支付结果
 */
-(void)TVSPage:(TVSWebPageType)pageType wxPayResult:(BOOL)result;

@optional
/*!
 * @brief H5页面关闭叮当智能语音服务
 * @param pageType 页面类型
 */
-(void)TVSUnbindAtPage:(TVSWebPageType)pageType;

@optional
/*!
 * @brief H5页面收到远程控制事件
 * @param pageType 页面类型
 * @param event 远程控制事件
 */
-(void)TVSPage:(TVSWebPageType)pageType receivedRemoteControlEvent:(UIEvent*)event;

@end

@interface TVSWebPage : NSObject

/*!
 * @brief 实例化
 * @return 实例
 */
+(instancetype)shared;

/*!
 * @brief 设置设备相关信息（用于领取会员）
 * @param deviceType 设备类型
 * @param deviceOEM 设备厂商
 * @param productId TVS 后台申请的产品 id
 * @param dsn 设备号
 */
-(void)setDeviceType:(NSString*)deviceType deviceOEM:(NSString*)deviceOEM productId:(NSString*)productId DSN:(NSString*)dsn;

/*!
 * @brief 打开会员相关 H5 页面
 * @warning 暂时只支持微信支付
 * @param pageType H5 页面类型
 * @param fromViewController 起始页面
 * @param title 标题，如果传空会自动读取网页标题
 * @param delegate 页面回调
 * @return 是否成功
 */
-(BOOL)enterPage:(TVSWebPageType)pageType fromViewController:(UIViewController*)fromViewController title:(NSString*)title delegate:(id<TVSWebPageDelegate>)delegate;

@end
