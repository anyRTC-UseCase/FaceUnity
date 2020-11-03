//
//  ARFaceUnityViewController.m
//  AR-Video-With-FaceUnity-iOS
//
//  Created by 余生丶 on 2020/10/23.
//

#import "ARFaceUnityViewController.h"
#import <ARtcKit/ARtcEngineKit.h>
#import "CapturerManager.h"
#import "VideoProcessingManager.h"
#import "FUManager.h"
#import "FUAPIDemoBar.h"
#import <Masonry/Masonry.h>
#import <AGMRenderer/AGMRenderer.h>
#import "FUCamera.h"
#import "FUOpenGLView.h"

#import "FUTestRecorder.h"

@interface ARFaceUnityViewController ()<ARtcEngineDelegate,FUAPIDemoBarDelegate, ARVideoSourceProtocol, FUCameraDelegate>

@property (nonatomic, strong) CapturerManager *capturerManager;
@property (nonatomic, strong) FUManager *videoFilter;
@property (nonatomic, strong) VideoProcessingManager *processingManager;
@property (nonatomic, strong) ARtcEngineKit *rtcEngineKit;
@property (nonatomic, strong) IBOutlet UIView *localView;

@property (weak, nonatomic) IBOutlet UIView *remoteView;

@property (nonatomic, strong) IBOutlet UIButton *switchBtn;
@property (nonatomic, strong) IBOutlet UIButton *remoteMirrorBtn;
@property (nonatomic, strong) IBOutlet UILabel *beautyStatus;
@property (nonatomic, strong) IBOutlet UIView *missingAuthpackLabel;
@property (nonatomic, strong) ARtcVideoCanvas *videoCanvas;
@property (nonatomic, assign) ARVideoMirrorMode localVideoMirrored;
@property (nonatomic, assign) ARVideoMirrorMode remoteVideoMirrored;
@property (nonatomic, strong) AGMEAGLVideoView *glVideoView;
/**faceU */
@property(nonatomic, strong) FUAPIDemoBar *demoBar;

@property (nonatomic, strong) FUCamera *fuCamera;
@property (nonatomic, strong) FUOpenGLView *fuGlView;

@end

@implementation ARFaceUnityViewController
@synthesize consumer;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [[FUTestRecorder shareRecorder] setupRecord];
    self.remoteView.hidden = YES;
    
    /** load Faceu */
    [self setupFaceUnity];
    
    // 初始化 rte engine
    self.rtcEngineKit = [ARtcEngineKit sharedEngineWithAppId:appID delegate:self];
    [self.rtcEngineKit setChannelProfile:ARChannelProfileiveBroadcasting];
    [self.rtcEngineKit setClientRole:ARClientRoleBroadcaster];
    [self.rtcEngineKit enableVideo];
    
    ARVideoEncoderConfiguration *config = [[ARVideoEncoderConfiguration alloc] initWithSize:CGSizeMake(1280, 720) frameRate:ARVideoFrameRateFps15 bitrate:500 orientationMode:ARVideoOutputOrientationModeFixedPortrait];
    [self.rtcEngineKit setVideoEncoderConfiguration:config];
    
#if 1 // 设置 1 测试 anyRTC 自采集自渲染，设置 0 测试 FU 自采集自渲染
    // init process manager
    self.processingManager = [[VideoProcessingManager alloc] init];
    
    // init capturer, it will push pixelbuffer to rtc channel
    AGMCapturerVideoConfig *videoConfig = [AGMCapturerVideoConfig defaultConfig];
    videoConfig.sessionPreset = AVCaptureSessionPreset1280x720;
    videoConfig.fps = 30;
    videoConfig.pixelFormat =  AGMVideoPixelFormatNV12;
    self.capturerManager = [[CapturerManager alloc] initWithVideoConfig:videoConfig delegate:self.processingManager];
    
    // add FaceUnity filter and add to process manager
    self.videoFilter = [FUManager shareManager];
    self.videoFilter.enabled = YES;
    [self.processingManager addVideoFilter:self.videoFilter];
    
    [self.capturerManager startCapture];
    
    // set up local video to render your local camera preview
//    self.videoCanvas = [ARtcVideoCanvas new];
//    self.videoCanvas.uid = 0;
//    // the view to be binded
//    self.videoCanvas.view = self.localView;
//    self.videoCanvas.renderMode = ARVideoRenderModeHidden;
//    self.videoCanvas.mirrorMode = ARVideoMirrorModeDisabled;
//    [self.rtcEngineKit setupLocalVideo:self.videoCanvas];
    
    [self.localView layoutIfNeeded];
    self.glVideoView = [[AGMEAGLVideoView alloc] initWithFrame:self.localView.frame];
//    [self.glVideoView setRenderMode:(AGMRenderMode_Fit)];
    [self.localView addSubview:self.glVideoView];
    [self.capturerManager setVideoView:self.glVideoView];
    // set custom capturer as video source
    [self.rtcEngineKit setVideoSource:self.capturerManager];
#else
    self.fuCamera = [[FUCamera alloc] initWithCameraPosition:AVCaptureDevicePositionFront captureFormat:kCVPixelFormatType_420YpCbCr8BiPlanarVideoRange];
    self.fuCamera.delegate = self;
    [self.fuCamera startCapture];
    
    self.fuGlView = [[FUOpenGLView alloc] initWithFrame:self.view.frame];
    [self.view insertSubview:self.fuGlView atIndex:0];
    self.localView.hidden = YES;
    self.demoBar.hidden = YES;
    [self.rtcEngineKit setVideoSource:self];
#endif

    // 注册通知
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(applicationDidChangeStatusBar) name:UIApplicationDidChangeStatusBarOrientationNotification object:nil];
    [self.rtcEngineKit joinChannelByToken:nil channelId:self.channelName uid:nil joinSuccess:^(NSString * _Nonnull channel, NSString * _Nonnull uid, NSInteger elapsed) {
        NSLog(@"joinChannelByToken Sucess");
    }];
}

- (void)applicationDidChangeStatusBar {
    UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;
    
    switch (orientation) {
        
        case UIDeviceOrientationPortrait:
            
            break;
            
        case UIDeviceOrientationLandscapeLeft:
            
            [self.fuCamera setCaptureVideoOrientation:(AVCaptureVideoOrientationLandscapeRight)];
            break;
            
            
        case UIDeviceOrientationLandscapeRight:
            
            [self.fuCamera setCaptureVideoOrientation:(AVCaptureVideoOrientationLandscapeLeft)];
            break;
            
        default:
            break;
    }
    
}

- (void)didOutputVideoSampleBuffer:(CMSampleBufferRef)sampleBuffer {
    CVPixelBufferRef pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer);
    CVPixelBufferLockBaseAddress(pixelBuffer, 0);
    [[FUTestRecorder shareRecorder] processFrameWithLog];
    
    CVPixelBufferRef buffer = [[FUManager shareManager] renderItemsToPixelBuffer:pixelBuffer];
    CMTime timestamp = CMSampleBufferGetPresentationTimeStamp(sampleBuffer);
    [self.consumer consumePixelBuffer:buffer withTimestamp:timestamp rotation:ARVideoRotationNone];
    [self.fuGlView displayPixelBuffer:buffer];
    CVPixelBufferUnlockBaseAddress(pixelBuffer, 0);
}

- (void)viewDidLayoutSubviews {
    self.glVideoView.frame = self.view.bounds;
    self.fuGlView.frame = self.view.bounds;
}

/// faceunity
- (void)setupFaceUnity{

    [[FUManager shareManager] loadFilter];
    [FUManager shareManager].flipx = YES;
    [FUManager shareManager].trackFlipx = YES;
    [FUManager shareManager].isRender = YES;
    [[FUManager shareManager] setAsyncTrackFaceEnable:NO];
    
    _demoBar = [[FUAPIDemoBar alloc] init];
    _demoBar.mDelegate = self;
    [self.view addSubview:_demoBar];
    [_demoBar mas_makeConstraints:^(MASConstraintMaker *make) {
        
        if (@available(iOS 11.0, *)) {
           
            make.left.mas_equalTo(self.view.mas_safeAreaLayoutGuideLeft);
            make.right.mas_equalTo(self.view.mas_safeAreaLayoutGuideRight);
            make.bottom.mas_equalTo(self.view.mas_safeAreaLayoutGuideBottom);
        
        } else {
        
            make.left.right.bottom.mas_equalTo(0);
        }

        make.height.mas_equalTo(195);
        
    }];
    
}


//MARK: - FUAPIDemoBarDelegate

-(void)filterValueChange:(FUBeautyParam *)param{
    [[FUManager shareManager] filterValueChange:param];
}

-(void)switchRenderState:(BOOL)state{
    [FUManager shareManager].isRender = state;
}

-(void)bottomDidChange:(int)index{
    if (index < 3) {
        [[FUManager shareManager] setRenderType:FUDataTypeBeautify];
    }
    if (index == 3) {
        [[FUManager shareManager] setRenderType:FUDataTypeStrick];
    }
    
    if (index == 4) {
        [[FUManager shareManager] setRenderType:FUDataTypeMakeup];
    }
    if (index == 5) {
        
        [[FUManager shareManager] setRenderType:FUDataTypebody];
    }
}


/// release
- (void)dealloc {
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [[FUManager shareManager] destoryItems];
    
    [self.capturerManager stopCapture];
    [self.rtcEngineKit leaveChannel:nil];
    [self.rtcEngineKit stopPreview];
    [self.rtcEngineKit setVideoSource:nil];
    [ARtcEngineKit destroy];
}


- (IBAction)switchCamera:(UIButton *)button {
    [self.capturerManager switchCamera];
    [[FUManager shareManager] onCameraChange];
}

- (IBAction)toggleRemoteMirror:(UIButton *)button {
    self.remoteVideoMirrored = self.remoteVideoMirrored == ARVideoMirrorModeEnabled ? ARVideoMirrorModeDisabled : ARVideoMirrorModeEnabled;
    ARVideoEncoderConfiguration* config = [[ARVideoEncoderConfiguration alloc] initWithSize:CGSizeMake(720, 1280) frameRate:30 bitrate:0 orientationMode:ARVideoOutputOrientationModeAdaptative];
    config.mirrorMode = self.remoteVideoMirrored;
    [self.rtcEngineKit setVideoEncoderConfiguration:config];
}


- (IBAction)backBtnClick:(UIButton *)sender {
    
    [self dismissViewControllerAnimated:YES completion:nil];
    
}

/// firstRemoteVideoDecoded
- (void)rtcEngine:(ARtcEngineKit *)engine firstRemoteVideoDecodedOfUid:(NSString *)uid size:(CGSize)size elapsed:(NSInteger)elapsed {
    if (self.remoteView.hidden) {
        self.remoteView.hidden = NO;
    }
    
    ARtcVideoCanvas *videoCanvas = [[ARtcVideoCanvas alloc] init];
    videoCanvas.uid = uid;
    // Since we are making a simple 1:1 video chat app, for simplicity sake, we are not storing the UIDs. You could use a mechanism such as an array to store the UIDs in a channel.
    
    videoCanvas.view = self.remoteView;
    videoCanvas.renderMode = ARVideoRenderModeHidden;
    [self.rtcEngineKit setupRemoteVideo:videoCanvas];
    // Bind remote video stream to view
}

//MARK: - ARVideoSourceProtocol

- (ARVideoBufferType)bufferType {
    return ARVideoBufferTypePixelBuffer;
}

- (void)shouldDispose {
    
}

- (BOOL)shouldInitialize {
    return YES;
}

- (void)shouldStart {
    
}

- (void)shouldStop {
    
}

@end

