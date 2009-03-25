// Copyright 2008 Cyrus Najmabadi
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#import "WarningView.h"

#import "ColorCache.h"
#import "FontCache.h"
#import "ImageCache.h"
#import "Model.h"

@interface WarningView()
@property (retain) UIImageView* imageView_;
@property (retain) UILabel* label_;
@end


@implementation WarningView

const NSInteger LABEL_X = 52;
const NSInteger TOP_BUFFER = 5;

@synthesize imageView_;
@synthesize label_;

property_wrapper(UIImageView*, imageView, ImageView);
property_wrapper(UILabel*, label, Label);

- (void) dealloc {
    self.imageView = nil;
    self.label = nil;

    [super dealloc];
}


- (id) initWithText:(NSString*) text {
    if (self = [super initWithFrame:CGRectZero]) {
        self.autoresizesSubviews = YES;
        self.backgroundColor = [UIColor groupTableViewBackgroundColor];

        self.label = [[[UILabel alloc] initWithFrame:CGRectZero] autorelease];
        self.label.text = text;
        self.label.numberOfLines = 0;
        self.label.backgroundColor = [UIColor clearColor];
        self.label.font = [FontCache footerFont];
        self.label.textColor = [ColorCache footerColor];
        self.label.shadowColor = [UIColor whiteColor];
        self.label.shadowOffset = CGSizeMake(0, 1);
        self.label.textAlignment = UITextAlignmentCenter;

        self.imageView = [[[UIImageView alloc] initWithImage:[ImageCache warning32x32]] autorelease];

        [self addSubview:self.imageView];
        [self addSubview:self.label];
    }

    return self;
}


- (Model*) model {
    return [Model model];
}


+ (WarningView*) viewWithText:(NSString*) text {
    return [[[WarningView alloc] initWithText:text] autorelease];
}

- (void) layoutSubviews {
    {
        NSString* text = self.label.text;
        CGRect frame = self.label.frame;
        frame.origin.x = LABEL_X;
        frame.origin.y = TOP_BUFFER;
        frame.size.width = self.frame.size.width - 10 - frame.origin.x;
        frame.size.height = [text sizeWithFont:[FontCache footerFont]
                             constrainedToSize:CGSizeMake(frame.size.width, 2000)
                                 lineBreakMode:UILineBreakModeWordWrap].height;
        self.label.frame = frame;
    }

    {
        CGRect frame = self.imageView.frame;
        frame.origin.x = 20;
        frame.origin.y = MAX(self.label.frame.origin.y, self.label.frame.origin.y + (int)((self.label.frame.size.height - [ImageCache warning32x32].size.height) / 2.0));
        self.imageView.frame = frame;
    }
}

- (CGFloat) height {
    double imageHeight = [ImageCache warning32x32].size.height;

    double width;
    if ([self.model screenRotationEnabled] &&
        UIDeviceOrientationIsLandscape([UIDevice currentDevice].orientation)) {
        width = [UIScreen mainScreen].bounds.size.height;
    } else {
        width = [UIScreen mainScreen].bounds.size.width;
    }

    NSInteger labelX = LABEL_X;
    NSInteger labelWidth = width - 10 - labelX;
    NSString* text = self.label.text;
    double labelHeight = [text sizeWithFont:[FontCache footerFont]
                          constrainedToSize:CGSizeMake(labelWidth, 2000)
                              lineBreakMode:UILineBreakModeWordWrap].height;

    return TOP_BUFFER + MAX(imageHeight, labelHeight);
}

@end