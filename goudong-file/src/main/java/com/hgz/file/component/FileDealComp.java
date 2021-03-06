package com.hgz.file.component;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.hgz.common.util.DateUtil;
import com.hgz.file.model.file.*;
import com.hgz.file.service.ShareFileService;
import com.hgz.file.service.ShareService;
import com.hgz.file.service.UserService;
import com.hgz.file.config.es.FileSearch;
import com.hgz.file.io.GouDongFile;
import com.hgz.file.mapper.FileMapper;
import com.hgz.file.mapper.MusicMapper;
import com.hgz.file.mapper.UserFileMapper;
import com.hgz.file.util.HttpsUtils;
import com.hgz.file.util.GouDongFileUtil;
import com.hgz.file.util.TreeNode;
import com.hgz.fileoperation.factory.FileOperationFactory;
import com.hgz.fileoperation.operation.copy.Copier;
import com.hgz.fileoperation.operation.copy.domain.CopyFile;
import com.hgz.fileoperation.operation.download.Downloader;
import com.hgz.fileoperation.operation.download.domain.DownloadFile;
import com.hgz.fileoperation.operation.write.Writer;
import com.hgz.fileoperation.operation.write.domain.WriteFile;
import com.hgz.fileoperation.util.FileOperationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.flac.FlacFileReader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * ????????????????????????
 *
 * @author CunTouGou
 * @date 2022/4/21 14:07
 */
@Slf4j
@Component
public class FileDealComp {
    @Resource
    private UserFileMapper userFileMapper;
    @Resource
    private FileMapper fileMapper;
    @Resource
    private UserService userService;
    @Resource
    private ShareService shareService;
    @Resource
    private ShareFileService shareFileService;
    @Resource
    private FileOperationFactory fileOperationFactory;
    @Resource
    private MusicMapper musicMapper;
    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public static Executor exec = Executors.newFixedThreadPool(10);

    /**
     * ?????????????????????
     *
     * ??????1: ????????????????????? savefilePath ?????????????????? ??????.txt ??????????????????????????? ??????(1).txt
     * ??????2??? ????????????????????? savefilePath ?????????????????? ??????.txt ??????????????????????????? ??????(1).txt
     * @param userFile ????????????
     * @param savefilePath ????????????
     * @return ???????????????
     */
    public String getRepeatFileName(UserFile userFile, String savefilePath) {
        String fileName = userFile.getFileName();
        String extendName = userFile.getExtendName();
        Integer deleteFlag = userFile.getDeleteFlag();
        Long userId = userFile.getUserId();
        int isDir = userFile.getIsDir();
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFilePath, savefilePath)
                .eq(UserFile::getDeleteFlag, deleteFlag)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getFileName, fileName)
                .eq(UserFile::getIsDir, isDir);
        if (userFile.getIsDir() == 0) {
            lambdaQueryWrapper.eq(UserFile::getExtendName, extendName);
        }
        List<UserFile> list = userFileMapper.selectList(lambdaQueryWrapper);
        if (list == null) {
            return fileName;
        }
        if (list.isEmpty()) {
            return fileName;
        }
        int i = 0;

        while (list != null && !list.isEmpty()) {
            i++;
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(UserFile::getFilePath, savefilePath)
                    .eq(UserFile::getDeleteFlag, deleteFlag)
                    .eq(UserFile::getUserId, userId)
                    .eq(UserFile::getFileName, fileName + "(" + i + ")")
                    .eq(UserFile::getIsDir, isDir);
            if (userFile.getIsDir() == 0) {
                lambdaQueryWrapper1.eq(UserFile::getExtendName, extendName);
            }
            list = userFileMapper.selectList(lambdaQueryWrapper1);
        }

        return fileName + "(" + i + ")";

    }

    /**
     * ?????????????????????
     *
     * 1?????????????????????????????????????????????????????????????????????,?????????????????????????????????????????????????????????????????????????????????????????????
     * 2???????????????
     *
     * @param sessionUserId ??????????????????id
     */
    public void restoreParentFilePath(GouDongFile fileoperationFile1, Long sessionUserId) {

        GouDongFile gouDongFile = new GouDongFile(fileoperationFile1.getPath(), fileoperationFile1.isDirectory());
        if (gouDongFile.isFile()) {
            gouDongFile = gouDongFile.getParentFile();
        }
        while(gouDongFile.getParent() != null) {
            String fileName = gouDongFile.getName();
            String parentFilePath = gouDongFile.getParent();

            LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserFile::getFilePath, parentFilePath)
                    .eq(UserFile::getFileName, fileName)
                    .eq(UserFile::getDeleteFlag, 0)
                    .eq(UserFile::getIsDir, 1)
                    .eq(UserFile::getUserId, sessionUserId);
            List<UserFile> userFileList = userFileMapper.selectList(lambdaQueryWrapper);
            if (userFileList.size() == 0) {
                UserFile userFile = GouDongFileUtil.getGouDongDir(sessionUserId, parentFilePath, fileName);
                try {
                    userFileMapper.insert(userFile);
                } catch (Exception e) {
                    //ignore
                }
            }
            gouDongFile = new GouDongFile(parentFilePath, true);
        }
    }


    /**
     * ??????????????????????????????
     *
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * @param filePath ????????????
     * @param sessionUserId ??????????????????id
     */
    public void deleteRepeatSubDirFile(String filePath, Long sessionUserId) {
        log.debug("??????????????????"+filePath);
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.select(UserFile::getFileName, UserFile::getFilePath)
                .likeRight(UserFile::getFilePath, GouDongFileUtil.formatLikePath(filePath))
                .eq(UserFile::getIsDir, 1)
                .eq(UserFile::getDeleteFlag, 0)
                .eq(UserFile::getUserId, sessionUserId)
                .groupBy(UserFile::getFilePath, UserFile::getFileName)
                .having("count(fileName) >= 2");
        List<UserFile> repeatList = userFileMapper.selectList(lambdaQueryWrapper);

        for (UserFile userFile : repeatList) {
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(UserFile::getFilePath, userFile.getFilePath())
                    .eq(UserFile::getFileName, userFile.getFileName())
                    .eq(UserFile::getDeleteFlag, "0");
            List<UserFile> userFiles = userFileMapper.selectList(lambdaQueryWrapper1);
            for (int i = 0; i < userFiles.size() - 1; i ++) {
                userFileMapper.deleteById(userFiles.get(i).getUserFileId());
            }
        }
    }

    /**
     * ?????????????????????????????????????????????????????????
     * @param treeNode ?????????
     * @param id ?????????id
     * @param filePath ????????????
     * @param nodeNameQueue ??????
     * @return ?????????
     */
    public TreeNode insertTreeNode(TreeNode treeNode, long id, String filePath, Queue<String> nodeNameQueue){

        List<TreeNode> childrenTreeNodes = treeNode.getChildren();
        String currentNodeName = nodeNameQueue.peek();
        if (currentNodeName == null){
            return treeNode;
        }

        GouDongFile gouDongFile = new GouDongFile(filePath, currentNodeName, true);
        filePath = gouDongFile.getPath();

        //1??????????????????????????????????????????????????????
        if (!isExistPath(childrenTreeNodes, currentNodeName)){
            //??????
            TreeNode resultTreeNode = new TreeNode();

            resultTreeNode.setFilePath(filePath);
            resultTreeNode.setLabel(nodeNameQueue.poll());
            resultTreeNode.setId(++id);

            childrenTreeNodes.add(resultTreeNode);

        }else{  //2????????????????????????
            nodeNameQueue.poll();
        }

        if (nodeNameQueue.size() != 0) {
            for (int i = 0; i < childrenTreeNodes.size(); i++) {

                TreeNode childrenTreeNode = childrenTreeNodes.get(i);
                if (currentNodeName.equals(childrenTreeNode.getLabel())){
                    childrenTreeNode = insertTreeNode(childrenTreeNode, id * 10, filePath, nodeNameQueue);
                    childrenTreeNodes.remove(i);
                    childrenTreeNodes.add(childrenTreeNode);
                    treeNode.setChildren(childrenTreeNodes);
                }

            }
        }else{
            treeNode.setChildren(childrenTreeNodes);
        }

        return treeNode;

    }

    /**
     * ????????????????????????????????????????????????
     * @param childrenTreeNodes ?????????
     * @param path ??????
     * @return ????????????
     */
    public boolean isExistPath(List<TreeNode> childrenTreeNodes, String path){
        boolean isExistPath = false;

        try {
            for (int i = 0; i < childrenTreeNodes.size(); i++){
                if (path.equals(childrenTreeNodes.get(i).getLabel())){
                    isExistPath = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return isExistPath;
    }


    /**
     * ??????????????????Id??????Elasticsearch
     * @param userFileId ????????????Id
     */
    public void uploadESByUserFileId(String userFileId) {

        try {
            Map<String, Object> param1 = new HashMap<>();
            param1.put("userFileId", userFileId);

            List<FileSearch> fileSearches = fileMapper.selectFileListToElasticSearch();

            if (fileSearches != null && fileSearches.size() > 0){
                FileSearch fileSearch = new FileSearch();
                BeanUtil.copyProperties(fileSearches, fileSearch);
                for (FileSearch search : fileSearches) {
                    elasticsearchClient.index(i -> i.index("filesearch").id(search.getUserFileId()).document(search));
                }
            }

            // List<UserFile> userfileResult = userFileMapper.selectByMap(param1);
            // List<FileBean> fileResult = fileMapper.selectByMap(param1);

            // if (userfileResult != null && userfileResult.size() > 0 || fileResult != null && fileResult.size() > 0){
            //     FileSearch fileSearch = new FileSearch();
            //     BeanUtil.copyProperties(userfileResult.get(0), fileSearch);
            //     BeanUtil.copyProperties(fileResult.get(0), fileSearch);
            //     elasticsearchClient.index(i -> i.index("filesearch").id(fileSearch.getUserFileId()).document(fileSearch));
            // }
        } catch (Exception e) {
            log.debug("ES????????????????????????????????????");
        }
    }

    /**
     * ??????????????????Id??????Elasticsearch
     * @param userFileId ????????????Id
     */
    public void deleteESByUserFileId(String userFileId) {
        exec.execute(()->{
            try {
                elasticsearchClient.delete(d -> d
                        .index("filesearch")
                        .id(userFileId));
            } catch (Exception e) {
                log.debug("ES????????????????????????????????????");
            }
        });


    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     * @param shareBatchNum ???????????????
     * @param extractionCode  ?????????
     * @param token ??????token
     * @param userFileId ????????????Id
     * @return ???????????????
     */
    public boolean checkAuthDownloadAndPreview(String shareBatchNum,
                                               String extractionCode,
                                               String token,
                                               String userFileId) {
        log.debug("?????????????????????shareBatchNum:{}, extractionCode:{}, token:{}, userFileId{}" , shareBatchNum, extractionCode, token, userFileId);
        UserFile userFile = userFileMapper.selectById(userFileId);
        log.debug(JSON.toJSONString(userFile));
        if ("undefined".equals(shareBatchNum)  || StringUtils.isEmpty(shareBatchNum)) {

            Long userId = userService.getUserIdByToken(token);
            log.debug(JSON.toJSONString("????????????session??????id???" + userId));
            if (userId == null) {
                return false;
            }
            log.debug("??????????????????id???" + userFile.getUserId());
            log.debug("????????????id:" + userId);
            if (userFile.getUserId().longValue() != userId) {
                log.info("??????id??????????????????????????????");
                return false;
            }
        } else {
            Map<String, Object> param = new HashMap<>();
            param.put("shareBatchNum", shareBatchNum);
            List<Share> shareList = shareService.listByMap(param);
            //???????????????
            if (shareList.size() <= 0) {
                log.info("?????????????????????????????????????????????");
                return false;
            }
            Integer shareType = shareList.get(0).getShareType();
            if (1 == shareType) {
                //???????????????
                if (!shareList.get(0).getExtractionCode().equals(extractionCode)) {
                    log.info("????????????????????????????????????");
                    return false;
                }
            }
            param.put("userFileId", userFileId);
            List<ShareFile> shareFileList = shareFileService.listByMap(param);
            if (shareFileList.size() <= 0) {
                log.info("??????id????????????????????????????????????????????????");
                return false;
            }
        }
        return true;
    }

    /**
     * ????????????
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????
     * @param fileBean ????????????
     * @param userFile ??????????????????
     * @return ????????????
     */
    public String copyFile(FileBean fileBean, UserFile userFile) {
        Copier copier = fileOperationFactory.getCopier();
        Downloader downloader = fileOperationFactory.getDownloader(fileBean.getStorageType());
        DownloadFile downloadFile = new DownloadFile();
        downloadFile.setFileUrl(fileBean.getFileUrl());
        CopyFile copyFile = new CopyFile();
        copyFile.setExtendName(userFile.getExtendName());
        String fileUrl = copier.copy(downloader.getInputStream(downloadFile), copyFile);
        if (downloadFile.getOssClient() != null) {
            downloadFile.getOssClient().shutdown();
        }
        fileBean.setFileUrl(fileUrl);
        fileBean.setFileId(null);
        fileMapper.insert(fileBean);
        userFile.setFileId(fileBean.getFileId());
        userFile.setUploadTime(DateUtil.getCurrentTime());
        userFileMapper.updateById(userFile);
        return fileUrl;
    }

    /**
     * ????????????????????????
     * @param fileUrl ????????????
     * @param storageType ????????????
     * @return ?????????
     * @throws IOException ??????????????????
     */
    public String getIdentifierByFile(String fileUrl, int storageType) throws IOException {
        DownloadFile downloadFile = new DownloadFile();
        downloadFile.setFileUrl(fileUrl);
        InputStream inputStream = fileOperationFactory.getDownloader(storageType).getInputStream(downloadFile);
        return DigestUtils.md5Hex(inputStream);
    }

    /**
     * ?????????????????????
     * @param storageType ????????????
     * @param fileUrl ????????????
     * @param inputStream ???????????????
     * @throws IOException ??????????????????
     */
    public void saveFileInputStream(int storageType, String fileUrl, InputStream inputStream) throws IOException {
        Writer writer1 = fileOperationFactory.getWriter(storageType);
        WriteFile writeFile = new WriteFile();
        writeFile.setFileUrl(fileUrl);
        int fileSize = inputStream.available();
        writeFile.setFileSize(fileSize);
        writer1.write(inputStream, writeFile);
    }

    /**
     * ??????????????????
     * @param fileName ?????????
     * @param filePath ????????????
     * @param userId ??????id
     * @return ????????????
     */
    public boolean isDirExist(String fileName, String filePath, long userId){
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName)
                .eq(UserFile::getFilePath, GouDongFile.formatPath(filePath))
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getDeleteFlag, 0)
                .eq(UserFile::getIsDir, 1);
        List<UserFile> list = userFileMapper.selectList(lambdaQueryWrapper);
        if (list != null && !list.isEmpty()) {
            return true;
        }
        return false;
    }


    /**
     * ??????????????????
     * @param extendName ???????????????
     * @param storageType ????????????
     * @param fileUrl ????????????
     * @param fileId ??????id
     */
    public void parseMusicFile(String extendName, int storageType, String fileUrl, String fileId) {
        File outFile = null;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            if ("mp3".equalsIgnoreCase(extendName) || "flac".equalsIgnoreCase(extendName)) {
                Downloader downloader = fileOperationFactory.getDownloader(storageType);
                DownloadFile downloadFile = new DownloadFile();
                downloadFile.setFileUrl(fileUrl);
                inputStream = downloader.getInputStream(downloadFile);
                outFile = FileOperationUtils.getTempFile(fileUrl);
                if (!outFile.exists()) {
                    outFile.createNewFile();
                }
                fileOutputStream = new FileOutputStream(outFile);
                IOUtils.copy(inputStream, fileOutputStream);
                Music music = new Music();
                music.setMusicId(IdUtil.getSnowflakeNextIdStr());
                music.setFileId(fileId);

                Tag tag = null;
                AudioHeader audioHeader = null;
                if ("mp3".equalsIgnoreCase(extendName)) {
                    MP3File f = (MP3File) AudioFileIO.read(outFile);
                    tag = f.getTag();
                    audioHeader = f.getAudioHeader();
                    MP3File mp3file = new MP3File(outFile);
                    if (mp3file.hasID3v2Tag()) {
                        AbstractID3v2Tag id3v2Tag = mp3file.getID3v2TagAsv24();
                        AbstractID3v2Frame frame = (AbstractID3v2Frame) id3v2Tag.getFrame("APIC");
                        FrameBodyAPIC body;
                        if (frame != null && !frame.isEmpty()) {
                            body = (FrameBodyAPIC) frame.getBody();
                            byte[] imageData = body.getImageData();
                            music.setAlbumImage(Base64.getEncoder().encodeToString(imageData));
                        }
                        if (tag != null) {
                            music.setArtist(tag.getFirst(FieldKey.ARTIST));
                            music.setTitle(tag.getFirst(FieldKey.TITLE));
                            music.setAlbum(tag.getFirst(FieldKey.ALBUM));
                            music.setYear(tag.getFirst(FieldKey.YEAR));
                            try {
                                music.setTrack(tag.getFirst(FieldKey.TRACK));
                            } catch (Exception e) {
                                // ignore
                            }

                            music.setGenre(tag.getFirst(FieldKey.GENRE));
                            music.setComment(tag.getFirst(FieldKey.COMMENT));
                            music.setLyrics(tag.getFirst(FieldKey.LYRICS));
                            music.setComposer(tag.getFirst(FieldKey.COMPOSER));
                            music.setAlbumArtist(tag.getFirst(FieldKey.ALBUM_ARTIST));
                            music.setEncoder(tag.getFirst(FieldKey.ENCODER));
                        }
                    }
                } else if ("flac".equalsIgnoreCase(extendName)) {
                    AudioFile f = new FlacFileReader().read(outFile);
                    tag = f.getTag();
                    audioHeader = f.getAudioHeader();
                    if (tag != null) {
                        music.setArtist(StringUtils.join(tag.getFields(FieldKey.ARTIST), ","));
                        music.setTitle(StringUtils.join(tag.getFields(FieldKey.TITLE), ","));
                        music.setAlbum(StringUtils.join(tag.getFields(FieldKey.ALBUM), ","));
                        music.setYear(StringUtils.join(tag.getFields(FieldKey.YEAR), ","));
                        music.setTrack(StringUtils.join(tag.getFields(FieldKey.TRACK), ","));
                        music.setGenre(StringUtils.join(tag.getFields(FieldKey.GENRE), ","));
                        music.setComment(StringUtils.join(tag.getFields(FieldKey.COMMENT), ","));
                        music.setLyrics(StringUtils.join(tag.getFields(FieldKey.LYRICS), ","));
                        music.setComposer(StringUtils.join(tag.getFields(FieldKey.COMPOSER), ","));
                        music.setAlbumArtist(StringUtils.join(tag.getFields(FieldKey.ALBUM_ARTIST), ","));
                        music.setEncoder(StringUtils.join(tag.getFields(FieldKey.ENCODER), ","));
                        List<Artwork> artworkList = tag.getArtworkList();
                        if (artworkList != null && !artworkList.isEmpty()) {
                            Artwork artwork = artworkList.get(0);
                            byte[] binaryData = artwork.getBinaryData();
                            music.setAlbumImage(Base64.getEncoder().encodeToString(binaryData));
                        }
                    }

                }

                if (audioHeader != null) {
                    music.setTrackLength(Float.parseFloat(audioHeader.getTrackLength() + ""));
                }

                if (StringUtils.isEmpty(music.getLyrics())) {
                    try {
                        String lyc = getLyc(music.getArtist(), music.getTitle());
                        music.setLyrics(lyc);
                    } catch (Exception e) {
                        log.info(e.getMessage());
                    }
                }
                musicMapper.insert(music);
            }
        } catch (Exception e) {
            log.error("???????????????????????????", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(fileOutputStream);
            if (outFile != null) {
                if (outFile.exists()) {
                    outFile.delete();
                }
            }
        }
    }

    /**
     * ??????QQ????????????
     * @param singerName ?????????
     * @param mp3Name ?????????
     * @return ??????
     */
    public String getLyc(String singerName, String mp3Name) {

        String s = HttpsUtils.doGetString("https://c.y.qq.com/splcloud/fcgi-bin/smartbox_new.fcg?_=1651992748984&cv=4747474&ct=24&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=1&uin=0&g_tk_new_20200303=5381&g_tk=5381&hostUin=0&is_xml=0&key=" + mp3Name.replaceAll(" ", ""));
        Map map = JSON.parseObject(s, Map.class);
        Map data = (Map) map.get("data");
        Map song = (Map) data.get("song");
        List<Map> list = (List<Map>) song.get("itemlist");
        String singer = "";
        String id = "";
        String mid = "";
        boolean isMatch = false;
        for (Map item : list) {
            singer = (String) item.get("singer");
            id = (String) item.get("id");
            mid = (String) item.get("mid");
            try {
                String singer1 = PinyinHelper.convertToPinyinString(singerName.replaceAll(" ", ""), ",", PinyinFormat.WITHOUT_TONE);
                String singer2 = PinyinHelper.convertToPinyinString(singer.replaceAll(" ", ""), ",", PinyinFormat.WITHOUT_TONE);
                if (singer1.contains(singer2) || singer2.contains(singer1)) {
                    isMatch = true;
                    break;
                }
            } catch (PinyinException e) {
                e.printStackTrace();
            }

        }
        if (!isMatch) {
            for (Map item : list) {
                singer = (String) item.get("singer");
                id = String.valueOf(item.get("id"));
                mid = String.valueOf(item.get("mid"));
                try {
                    String singer2 = PinyinHelper.convertToPinyinString(singer.replaceAll(" ", ""), ",", PinyinFormat.WITHOUT_TONE);
                    String singer3 = PinyinHelper.convertToPinyinString(mp3Name.replaceAll(" ", ""), ",", PinyinFormat.WITHOUT_TONE);
                    if (singer3.contains(singer2) || singer2.contains(singer3)) {
                        isMatch = true;
                        break;
                    }
                } catch (PinyinException e) {
                    e.printStackTrace();
                }

            }
        }

        if (!isMatch) {
            Map album = (Map) data.get("album");
            List<Map> albumlist = (List<Map>) album.get("itemlist");
            for (Map item : albumlist) {
                String mp3name = (String) item.get("name");
                singer = (String) item.get("singer");
                id = (String) item.get("id");
                mid = (String) item.get("mid");
                if (singer.equals(singerName) && mp3Name.equals(mp3name)) {
                    String res = HttpsUtils.doGetString("https://c.y.qq.com/v8/fcg-bin/musicmall.fcg?_=1652026128283&cv=4747474&ct=24&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=1&uin=0&g_tk_new_20200303=5381&g_tk=5381&cmd=get_album_buy_page&albummid=" + mid + "&albumid=0");
                    Map map1 = JSON.parseObject(res, Map.class);
                    Map data1 = (Map) map1.get("data");
                    List<Map> list1 = (List<Map>) data1.get("songlist");
                    for (Map item1 : list1) {
                        if (mp3Name.equals((String) item1.get("songname"))) {
                            id = String.valueOf(item1.get("songid"));
                            mid = String.valueOf(item1.get("songmid"));
                            isMatch = true;
                            break;
                        }
                    }
                    if (isMatch) {
                        break;
                    }

                }
            }
        }

        String s1 = HttpsUtils.doGetString("https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?_=1651993218842&cv=4747474&ct=24&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=1&uin=0&g_tk_new_20200303=5381&g_tk=5381&loginUin=0&" +
                "songmid="+mid+"&" +
                "musicid=" + id);
        Map map1 = JSON.parseObject(s1, Map.class);
        return (String) map1.get("lyric");
    }

}
