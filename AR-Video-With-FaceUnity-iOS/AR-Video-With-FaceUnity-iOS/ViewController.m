//
//  ViewController.m
//  AR-Video-With-FaceUnity-iOS
//
//  Created by 余生丶 on 2020/10/21.
//

#import "ViewController.h"
#import "ARFaceUnityViewController.h"

@interface ViewController ()

@property (weak, nonatomic) IBOutlet UITextField *channelTextField;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    NSString *segueid = segue.identifier;
    
    if ([segueid isEqualToString: @"FaceUnityIdentifier"]) {
        ARFaceUnityViewController *faceUnity = segue.destinationViewController;
        faceUnity.channelName = self.channelTextField.text;
    }
}

- (BOOL)shouldPerformSegueWithIdentifier:(NSString *)identifier sender:(id)sender {
    return (self.channelTextField.text.length != 0);
}


- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [self.channelTextField resignFirstResponder];
    
}


@end
