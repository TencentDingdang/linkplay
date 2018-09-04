//
//  TVSTSKM.h
//  TVSAccountSDK
//
//  Created by Rinc Liu on 3/9/2018.
//  Copyright © 2018 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>

/*!
 * @class TVSTSKM
 * @brief TVS TSKM
 */
@interface TVSTSKM : NSObject

/*!
 * @brief 实例化
 * @return 实例
 */
+(instancetype)shared;

/*!
 * @brief UniAccess
 * @warning 必须确保已登录！！
 * @param deviceGuid 设备 guid
 * @param deviceProductId 设备 productId
 * @param domain TSKM 服务领域
 * @param intent TSKM 服务意图
 * @param blobInfo 传入的参数字典
 * @param handler 回调，NSDictionary 类型参数为返回的字典数据
 */
-(void)uniAccessWithDeviceGuid:(NSString*)deviceGuid deviceProductId:(NSString*)deviceProductId domain:(NSString*)domain intent:(NSString*)intent blobInfo:(NSDictionary*)blobInfo handler:(void(^)(NSDictionary*))handler;

@end
