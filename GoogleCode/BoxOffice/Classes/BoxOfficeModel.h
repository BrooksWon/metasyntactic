//
//  BoxOfficeModel.h
//  BoxOfficeApplication
//
//  Created by Cyrus Najmabadi on 4/26/08.
//  Copyright 2008 Metasyntactic. All rights reserved.
//

#import "PosterCache.h"
#import "TrailerCache.h"
#import "AddressLocationCache.h"
#import "ReviewCache.h"
#import "XmlElement.h"
#import "Theater.h"
#import "NotificationCenter.h"
#import "SearchCache.h"

@interface BoxOfficeModel : NSObject {
    NotificationCenter* notificationCenter;
    
    PosterCache* posterCache;
    TrailerCache* trailerCache;
    AddressLocationCache* addressLocationCache;
    ReviewCache* reviewCache;
    SearchCache* SearchCache;
    
    NSInteger backgroundTaskCount;
    UIActivityIndicatorView* activityIndicatorView;
    UIView* activityView;
    
    NSInteger searchRadius;
    
    NSArray* moviesData;
    NSArray* theatersData;
    NSDictionary* supplementaryInformationData;
    NSDictionary* movieMap;
    
    NSMutableArray* favoriteTheatersData;
}

@property (retain) NotificationCenter* notificationCenter;
@property (retain) PosterCache* posterCache;
@property (retain) TrailerCache* trailerCache;
@property (retain) ReviewCache* reviewCache;
@property (retain) AddressLocationCache* addressLocationCache;
@property (retain) UIActivityIndicatorView* activityIndicatorView;
@property (retain) UIView* activityView;
@property (readonly) NSInteger backgroundTaskCount;

@property (retain) NSArray* moviesData;
@property (retain) NSArray* theatersData;
@property (retain) NSDictionary* supplementaryInformationData;
@property (retain) NSDictionary* movieMap;
@property (retain) NSMutableArray* favoriteTheatersData;

+ (BoxOfficeModel*) modelWithCenter:(NotificationCenter*) notificationCenter;

- (void) addBackgroundTask:(NSString*) description;
- (void) removeBackgroundTask:(NSString*) description;

- (NSInteger) selectedTabBarViewControllerIndex;
- (void) setSelectedTabBarViewControllerIndex:(NSInteger) index;

- (NSInteger) allMoviesSelectedSegmentIndex;
- (void) setAllMoviesSelectedSegmentIndex:(NSInteger) index;

- (BOOL) sortingMoviesByTitle;
- (BOOL) sortingMoviesByScore;
- (BOOL) sortingMoviesByReleaseDate;

- (NSInteger) allTheatersSelectedSegmentIndex;
- (void) setAllTheatersSelectedSegmentIndex:(NSInteger) index;

- (Movie*) currentlySelectedMovie;
- (Theater*) currentlySelectedTheater;
- (BOOL) currentlyShowingReviews;

- (void) setCurrentlyShowingReviews;
- (void) setCurrentlySelectedMovie:(Movie*) movie 
                           theater:(Theater*) theater;

- (NSString*) postalCode;
- (void) setPostalCode:(NSString*) postalCode;

- (NSInteger) searchRadius;
- (void) setSearchRadius:(NSInteger) searchRadius;

- (NSDate*) searchDate;
- (void) setSearchDate:(NSDate*) date;

- (NSDate*) lastQuickUpdateTime;
- (NSDate*) lastFullUpdateTime;

- (NSDictionary*) supplementaryInformation;
- (void) setSupplementaryInformation:(NSDictionary*) dictionary;

- (NSArray*) movies;
- (void) setMovies:(NSArray*) movies;

- (NSArray*) theaters;
- (void) setTheaters:(NSArray*) theaters;

- (UIImage*) posterForMovie:(Movie*) movie;
- (Location*) locationForAddress:(NSString*) address;
- (Location*) locationForPostalCode:(NSString*) postalCode;

- (NSMutableArray*) theatersShowingMovie:(Movie*) movie;
- (NSMutableArray*) moviesAtTheater:(Theater*) theater;
- (NSArray*) movieShowtimes:(Movie*) movie
                 forTheater:(Theater*) theater;

- (NSDictionary*) theaterDistanceMap;
- (NSArray*) theatersInRange:(NSArray*) theaters;

NSInteger compareMoviesByScore(id t1, id t2, void *context);
NSInteger compareMoviesByReleaseDate(id t1, id t2, void *context);
NSInteger compareMoviesByTitle(id t1, id t2, void *context);
NSInteger compareTheatersByName(id t1, id t2, void *context);
NSInteger compareTheatersByDistance(id t1, id t2, void *context);

- (XmlElement*) getPersonDetails:(NSString*) identifier;
- (void) setPersonDetails:(NSString*) identifier element:(XmlElement*) element;

- (XmlElement*) getMovieDetails:(NSString*) identifier;
- (void) setMovieDetails:(NSString*) identifier element:(XmlElement*) element;

- (NSMutableDictionary*) getSearchDates;
- (NSMutableDictionary*) getSearchResults;

- (NSString*) synopsisForMovie:(Movie*) movie;
- (NSInteger) scoreForMovie:(Movie*) movie;

- (NSArray*) trailersForMovie:(Movie*) movie;
- (NSArray*) reviewsForMovie:(Movie*) movie;

- (void) applicationWillTerminate;

- (NSMutableArray*) favoriteTheaters;
- (BOOL) isFavoriteTheater:(Theater*) theater;
- (void) addFavoriteTheater:(Theater*) theater;
- (void) removeFavoriteTheater:(Theater*) theater;

- (NSString*) noLocationInformationFound;

@end
