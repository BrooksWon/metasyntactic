//
//  Application.m
//  BoxOfficeApplication
//
//  Created by Cyrus Najmabadi on 4/29/08.
//  Copyright 2008 Metasyntactic. All rights reserved.
//

#import "Application.h"


@implementation Application

static NSRecursiveLock* gate = nil;
static NSString* supportFolder = nil;
static NSString* dataFolder = nil;
static NSString* searchFolder = nil;
static NSString* postersFolder = nil;
static NSString* reviewsFolder = nil;
static NSString* trailersFolder = nil;
static NSString* documentsFolder = nil;
static NSDateFormatter* dateFormatter = nil;
static UIColor* commandColor = nil;
static DifferenceEngine* differenceEngine = nil;
static NSString* starString = nil;

+ (void) initialize {
    if (self == [Application class]) {
        gate = [[NSRecursiveLock alloc] init];
        
        dateFormatter = [[NSDateFormatter alloc] init];
        
        commandColor = //[[UIColor colorWithRed:0.32 green:0.4 blue:0.55 alpha:1] retain];
            [[UIColor colorWithRed:0.196 green:0.309 blue:0.521 alpha:1] retain];
                
        differenceEngine = [[DifferenceEngine engine] retain];
    }
}

+ (void) createDirectory:(NSString*) folder {
    if (![[NSFileManager defaultManager] fileExistsAtPath:folder]) {
        NSError* error;
        [[NSFileManager defaultManager] createDirectoryAtPath:folder withIntermediateDirectories:YES attributes:nil error:&error];
    }    
}

+ (NSString*) documentsFolder {
    [gate lock];
    {
        if (documentsFolder == nil) {
            NSArray* paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, /*expandTilde:*/YES);
            NSString* folder = [paths objectAtIndex:0];
            
            [Application createDirectory:folder];
            
            documentsFolder = [folder retain];
        }
    }
    [gate unlock];
    
    return documentsFolder;
}

+ (NSString*) supportFolder {
    [gate lock];
    {
        if (supportFolder == nil) {
            NSArray* paths = NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, /*expandTilde:*/YES);
            
            NSString* executableName = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleExecutable"];
            NSString* folder = [[paths objectAtIndex:0] stringByAppendingPathComponent:executableName];
            
            [Application createDirectory:folder];
            
            supportFolder = [folder retain];
        }
    }
    [gate unlock];
    
    return supportFolder;
}

+ (NSString*) dataFolder {
    [gate lock];
    {
        if (dataFolder == nil) {
            NSString* parent = [Application supportFolder];
            NSString* folder = [parent stringByAppendingPathComponent:@"Data"];
            
            [Application createDirectory:folder];
            
            dataFolder = [folder retain];
        }
    }
    [gate unlock];
    
    return dataFolder;
}

+ (NSString*) searchFolder {
    [gate lock];
    {
        if (searchFolder == nil) {
            NSString* parent = [Application supportFolder];
            NSString* folder = [parent stringByAppendingPathComponent:@"Search"];
            
            [Application createDirectory:folder];
            
            searchFolder = [folder retain];
        }
    }
    [gate unlock];
    
    return searchFolder;
}

+ (NSString*) movieMapFile {
    return [[Application dataFolder] stringByAppendingPathComponent:@"MovieMap.plist"];
}

+ (NSString*) moviesFile {
    return [[Application dataFolder] stringByAppendingPathComponent:@"Movies.plist"];
}

+ (NSString*) ratingsFile {
    return [[Application dataFolder] stringByAppendingPathComponent:@"Ratings.plist"];
}

+ (NSString*) theatersFile {
    return [[Application dataFolder] stringByAppendingPathComponent:@"Theaters.plist"];
}

+ (NSString*) reviewsFolder {
    [gate lock];
    {
        if (reviewsFolder == nil) {
            NSString* parent = [Application supportFolder];
            NSString* folder = [parent stringByAppendingPathComponent:@"Reviews"];
            
            [Application createDirectory:folder];
            
            reviewsFolder = [folder retain];
        }
    }
    [gate unlock];
    
    return reviewsFolder;
}

+ (NSString*) trailersFolder {
    [gate lock];
    {
        if (trailersFolder == nil) {
            NSString* parent = [Application supportFolder];
            NSString* folder = [parent stringByAppendingPathComponent:@"Trailers"];
            
            [Application createDirectory:folder];
            
            trailersFolder = [folder retain];
        }
    }
    [gate unlock];
    
    return trailersFolder;
}

+ (NSString*) postersFolder {
    [gate lock];
    {
        if (postersFolder == nil) {
            NSString* parent = [Application supportFolder];
            NSString* folder = [parent stringByAppendingPathComponent:@"Posters"];
            
            [Application createDirectory:folder];
            
            postersFolder = [folder retain];
        }
    }
    [gate unlock];
    
    return postersFolder;
}

+ (NSString*) formatShortTime:(NSDate*) date {
    NSString* result;
    [gate lock];
    {
        [dateFormatter setDateStyle:NSDateFormatterNoStyle];
        [dateFormatter setTimeStyle:NSDateFormatterShortStyle];
        result = [dateFormatter stringFromDate:date];
    }
    [gate unlock];
    return result;
}

+ (NSString*) formatShortDate:(NSDate*) date {
    NSString* result;
    [gate lock];
    {
        [dateFormatter setDateStyle:NSDateFormatterShortStyle];
        [dateFormatter setTimeStyle:NSDateFormatterNoStyle];
        result = [dateFormatter stringFromDate:date];
    }
    [gate unlock];
    return result;
}

+ (NSString*) formatLongDate:(NSDate*) date {
    NSString* result;
    [gate lock];
    {
        [dateFormatter setDateStyle:NSDateFormatterLongStyle];
        [dateFormatter setTimeStyle:NSDateFormatterNoStyle];
        result = [dateFormatter stringFromDate:date];
    }
    [gate unlock];
    return result;
}

+ (NSString*) formatFullDate:(NSDate*) date {
    NSString* result;
    [gate lock];
    {
        [dateFormatter setDateStyle:NSDateFormatterFullStyle];
        [dateFormatter setTimeStyle:NSDateFormatterNoStyle];
        result = [dateFormatter stringFromDate:date];
    }
    [gate unlock];
    return result;
}

+ (UIColor*) commandColor {
    return commandColor;
}

+ (void) openBrowser:(NSString*) address {
    if (address == nil) {
        return;
    }
    
    NSURL* url = [NSURL URLWithString:address];
    [[UIApplication sharedApplication] openURL:url];
}

+ (void) openMap:(NSString*) address {
    NSString* urlString =
    [NSString stringWithFormat:@"http://maps.google.com/maps?q=%@", 
     [address stringByAddingPercentEscapesUsingEncoding:NSISOLatin1StringEncoding]];
    
    [self openBrowser:urlString];
}

+ (void) makeCall:(NSString*) phoneNumber {
    if (![[[UIDevice currentDevice] model] isEqual:@"iPhone"]) {
        // can't make a phonecall if you're not an iPhone.
        return;
    }
    
    NSString* urlString = [NSString stringWithFormat:@"tel:%@", phoneNumber];
    
    [self openBrowser:urlString];   
}

+ (DifferenceEngine*) differenceEngine {
    NSAssert([NSThread isMainThread], @"Cannot access difference engine from main thread.");
    return differenceEngine;
}

+ (NSString*) searchHost {
    //return @"http://metaboxoffice6.appspot.com";
    return @"http://localhost:8086";
}

+ (NSMutableArray*) hosts {
    return [NSMutableArray arrayWithObjects:
            @"metaboxoffice",
            @"metaboxoffice2",
            @"metaboxoffice3",
            @"metaboxoffice4",
            @"metaboxoffice5",
            @"metaboxoffice6", nil]; 
}

+ (unichar) starCharacter {
    return (unichar)0x2605;
}

+ (NSString*) starString {
    if (starString == nil) {
        unichar c = [Application starCharacter];
        starString = [NSString stringWithCharacters:&c length:1];
    }
    
    return starString;
}

@end
