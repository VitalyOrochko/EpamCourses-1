package by.bsu.audioservice.dao.impl;

import by.bsu.audioservice.dao.AbstractAudioDAO;
import by.bsu.audioservice.entity.Audio;
import by.bsu.audioservice.entity.Genre;
import by.bsu.audioservice.entity.UserAccount;
import by.bsu.audioservice.exception.DAOException;
import by.bsu.audioservice.pool.ConnectionPool;
import by.bsu.audioservice.pool.ProxyConnection;
import by.bsu.audioservice.util.GenreUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Class AudioDAO
 *
 * Created by 7 on 18.08.2016.
 */
public class AudioDAO extends AbstractAudioDAO {
    /** Field SQL_SELECT_ALL_USER_AUDIOS */
    private static final String SQL_SELECT_ALL_USER_AUDIOS = "SELECT audios.singer, audios.name, genres.genre, audios.url," +
            " audios.price, audios.audio_id from (audios join audios_has_genre join account_has_audios join genres" +
            " on audios.audio_id = audios_has_genre.audio_id and audios_has_genre.genre_id = genres.genre_id)" +
            " where audios.audio_id = account_has_audios.audio_id and account_has_audios.account_id = ?;";

    /** Field SQL_SELECT_ALL_DEMO_AUDIO */
    private static final String SQL_SELECT_ALL_DEMO_AUDIO = "SELECT audios.name, audios.singer, audios.price, audios.audio_id, " +
            "genres.genre, demo_audios.url from audios join demo_audios join genres join audios_has_genre " +
            "on demo_audios.fk_audio_id = audios.audio_id and audios.audio_id = audios_has_genre.audio_id " +
            "and audios_has_genre.genre_id = genres.genre_id;";

    /** Field SQL_SELECT_AUDIOS_BY_GENRE */
    private static final String SQL_SELECT_AUDIOS_BY_GENRE = "SELECT audios.name, audios.singer, audios.price, audios.audio_id," +
            " genres.genre, demo_audios.url from audios join demo_audios join genres join audios_has_genre " +
            "on demo_audios.fk_audio_id = audios.audio_id and audios.audio_id = audios_has_genre.audio_id " +
            "and audios_has_genre.genre_id = genres.genre_id where genres.genre = ?;";

    /** Field SQL_SELECT_ALL_AUDIOS */
    private static final String SQL_SELECT_ALL_AUDIOS = "SELECT audios.audio_id, singer, name, price, url," +
            " genre from audios join genres join audios_has_genre on audios.audio_id = audios_has_genre.audio_id and" +
            " audios_has_genre.genre_id = genres.genre_id;";

    /** Field SQL_SELECT_AUDIO_BY_AUDIO_ID */
    private static final String SQL_SELECT_AUDIO_BY_AUDIO_ID = "SELECT audios.audio_id, singer, name, price, url," +
            " genre from audios join genres join audios_has_genre on audios.audio_id = audios_has_genre.audio_id and" +
            " audios_has_genre.genre_id = genres.genre_id and audios.audio_id = ?;";

    /** Field SQL_SELECT_AUDIO_ID_BY_URL */
    private static final String SQL_SELECT_AUDIO_ID_BY_URL = "SELECT audios.audio_id FROM audios WHERE audios.url = ?;";

    /** Field SQL_SELECT_AUDIO_BY_URL */
    private static final String SQL_SELECT_AUDIO_BY_URL = "SELECT audios.audio_id, singer, name, price, genre" +
            " FROM audios JOIN genres join audios_has_genre ON audios.audio_id = audios_has_genre.audio_id AND " +
            "audios_has_genre.genre_id = genres.genre_id WHERE audios.url = ?;";

    /** Field SQL_UPDATE_AUDIO_INFO_BY_ID */
    private static final String SQL_UPDATE_AUDIO_INFO_BY_ID = "UPDATE audios SET singer = ?, name = ?, price = ?" +
            " WHERE audio_id = ?;";

    /** Field SQL_INSERT_NEW_AUDIO */
    private static final String SQL_INSERT_NEW_AUDIO = "INSERT INTO audios (singer, name, url, price) VALUES (?, ?, ?, ?);";

    /** Field SQL_INSERT_DEMO_AUDIO */
    private static final String SQL_INSERT_DEMO_AUDIO = "INSERT INTO demo_audios(url, fk_audio_id) VALUES (?, ?);";

    /** Field SQL_INSERT_GENRE_TO_NEW_AUDIO */
    private static final String SQL_INSERT_GENRE_TO_NEW_AUDIO = "INSERT INTO audios_has_genre (genre_id, audio_id) VALUES (?, ?);";

    /** Field  SQL_SELECT_GENRE_ID_BY_GENRE */
    private static final String SQL_SELECT_GENRE_ID_BY_GENRE = "SELECT genres.genre_id from genres where genres.genre = ?;";

    /** Field SQL_SELECT_AUDIO_BY_SINGER_AND_AUDIO_NAME */
    private static final String SQL_SELECT_AUDIO_BY_SINGER_AND_AUDIO_NAME = "SELECT audios.singer, audios.name FROM audios" +
            " WHERE audios.singer = ? AND audios.name = ?;";

    /** Field SINGER */
    private static final String SINGER = "singer";

    /** Field GENRE */
    private static final String NAME = "name";

    /** Field GENRE */
    private static final String GENRE = "genre";

    /** Field GENRE_ID */
    private static final String GENRE_ID = "genre_id";

    /** Field URL */
    private static final String URL = "url";

    /** Field PRICE */
    private static final String PRICE = "price";

    /** Field AUDIO_ID */
    private static final String AUDIO_ID = "audio_id";

    /** Field instance */
    private static AudioDAO instance = AudioDAO.getInstance() ;

    /**
     * Instantiates a new AudioDAO
     */
    private AudioDAO(){
    }

    /**
     * Get instance of AudioDAO
     *
     * @return AudioDAO
     */
    public static AudioDAO getInstance(){
        if (instance == null){
            instance = new AudioDAO();
        }
        return instance;
    }

    /**
     * Method takeUserAccountAudios
     *
     * @param account of type UserAccount
     * @return ArrayList<Audio>
     * @throws DAOException
     */
    @Override
    public ArrayList<Audio> takeUserAccountAudios(UserAccount account) throws DAOException {
        ArrayList<Audio> audios = new ArrayList<>();
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
            PreparedStatement st = cn.prepareStatement(SQL_SELECT_ALL_USER_AUDIOS)){
            st.setLong(1, account.getUserAccountId());
            ResultSet rs = st.executeQuery();
            while (rs.next()){
                String singer = rs.getString(SINGER);
                String name = rs.getString(NAME);
                String genreStr = rs.getString(GENRE);
                Genre genre;
                if (genreStr.toUpperCase().equals(Genre.POP.toString())){
                    genre = Genre.POP;
                } else if(genreStr.toUpperCase().equals(Genre.ROCK.toString())){
                    genre = Genre.ROCK;
                } else {
                    genre = Genre.HIP_HOP;
                }
                String url = rs.getString(URL);
                Float price = rs.getFloat(PRICE);
                Long id = rs.getLong(AUDIO_ID);
                Audio audio = new Audio(id, singer, name, url, price, genre);
                audios.add(audio);
            }
        } catch (SQLException e) {
            throw new DAOException("SQLException in AudioDAO layer", e);
        }
        return audios;
    }

    /**
     * Method takeAllDemoAudio
     *
     * @return ArrayList<Audio>
     * @throws DAOException
     */
    @Override
    public ArrayList<Audio> takeAllDemoAudio() throws DAOException {
        ArrayList<Audio> demoAudios = null;
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
            PreparedStatement st = cn.prepareStatement(SQL_SELECT_ALL_DEMO_AUDIO)){
            demoAudios = new ArrayList<Audio>();
            ResultSet rs = st.executeQuery();
            while (rs.next()){
                String singer = rs.getString(SINGER);
                String name = rs.getString(NAME);
                String genreStr = rs.getString(GENRE);
                Genre genre;
                if (genreStr.toUpperCase().equals(Genre.POP.toString())){
                    genre = Genre.POP;
                } else if(genreStr.toUpperCase().equals(Genre.ROCK.toString())){
                    genre = Genre.ROCK;
                } else {
                    genre = Genre.HIP_HOP;
                }
                String url = rs.getString(URL);
                Float price = rs.getFloat(PRICE);
                Long id = rs.getLong(AUDIO_ID);
                Audio audio = new Audio(id, singer, name, url, price, genre);
                demoAudios.add(audio);
            }

        } catch (SQLException e) {
            throw new DAOException("SQLException in AudioDAO layer", e);
        }
        return demoAudios;
    }

    /**
     * Method takeAudiosByGenre
     *
     * @param genreStr of type String
     * @return ArrayList<Audio>
     * @throws DAOException
     */
    @Override
    public ArrayList<Audio> takeAudiosByGenre(String genreStr) throws DAOException {
        ArrayList<Audio> audios = new ArrayList<>();
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
        PreparedStatement st = cn.prepareStatement(SQL_SELECT_AUDIOS_BY_GENRE)){
            st.setString(1, genreStr);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                String singer = rs.getString(SINGER);
                String name = rs.getString(NAME);
                Genre genre;
                if (genreStr.toUpperCase().equals(Genre.POP.toString())) {
                    genre = Genre.POP;
                } else if (genreStr.toUpperCase().equals(Genre.ROCK.toString())) {
                    genre = Genre.ROCK;
                } else {
                    genre = Genre.HIP_HOP;
                }
                String url = rs.getString(URL);
                Float price = rs.getFloat(PRICE);
                Long id = rs.getLong(AUDIO_ID);
                Audio audio = new Audio(id, singer, name, url, price, genre);
                audios.add(audio);
            }
        } catch (SQLException e) {
            throw new DAOException("SQLException in AudioDAO layer", e);
        }
        return audios;
    }

    /**
     * Method takeAllAudios
     *
     * @return ArrayList<Audio>
     * @throws DAOException
     */
    @Override
    public ArrayList<Audio> takeAllAudios() throws DAOException {
        ArrayList<Audio> audios = null;
        try (ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
             PreparedStatement st = cn.prepareStatement(SQL_SELECT_ALL_AUDIOS)) {
            audios = new ArrayList<>();
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                String singer = rs.getString(SINGER);
                String name = rs.getString(NAME);
                String genreStr = rs.getString(GENRE);
                Genre genre;
                if (genreStr.toUpperCase().equals(Genre.POP.toString())) {
                    genre = Genre.POP;
                } else if (genreStr.toUpperCase().equals(Genre.ROCK.toString())) {
                    genre = Genre.ROCK;
                } else {
                    genre = Genre.HIP_HOP;
                }
                String url = rs.getString(URL);
                Float price = rs.getFloat(PRICE);
                Long id = rs.getLong(AUDIO_ID);
                Audio audio = new Audio(id, singer, name, url, price, genre);
                audios.add(audio);
            }
        } catch(SQLException e){
                throw new DAOException("SQLException in AudioDAO layer", e);
        }
        return audios;
    }

    /**
     * Method addAudio
     *
     * @param audio of type Audio
     * @return boolean
     * @throws DAOException
     */
    @Override
    public boolean addAudio(Audio audio) throws DAOException {
        boolean flag = false;
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
        PreparedStatement st = cn.prepareStatement(SQL_INSERT_NEW_AUDIO)){
            st.setString(1, audio.getSinger());
            st.setString(2, audio.getName());
            st.setString(3, audio.getUrl());
            st.setFloat(4, audio.getPrice());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            throw new DAOException("SQLException in AudioDAO layer", e);
        }
        return flag;
    }

    /**
     * Method addDemoAudio
     *
     * @param audio of type Audio
     * @param demoURL of type String
     * @return boolean
     * @throws DAOException
     */
    @Override
    public boolean addDemoAudio(Audio audio, String demoURL) throws DAOException {
        boolean flag = false;
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
        PreparedStatement st = cn.prepareStatement(SQL_INSERT_DEMO_AUDIO)){
            st.setString(1, demoURL);
            st.setLong(2, audio.getAudioId());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            throw new DAOException("SQLException in AudioDAO layer", e);
        }
        return flag;
    }

    /**
     * Method takeAudioIdByURL
     *
     * @param url of type String
     * @return Long
     * @throws DAOException
     */
    @Override
    public Long takeAudioIdByURL(String url) throws DAOException {
        Long id = null;
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
        PreparedStatement st = cn.prepareStatement(SQL_SELECT_AUDIO_ID_BY_URL)){
            st.setString(1, url);
            ResultSet rs = st.executeQuery();
            if (rs.next()){
                id = rs.getLong(AUDIO_ID);
            }
        } catch (SQLException e) {
            throw new DAOException("SQLException in AudioDAO layer", e);
        }
        return id;
    }

    /**
     * Method takeAudioByURL
     *
     * @param url of type String
     * @return Audio
     * @throws DAOException
     */
    @Override
    public Audio takeAudioByURL(String url) throws DAOException {
        Audio audio = null;
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
        PreparedStatement st = cn.prepareStatement(SQL_SELECT_AUDIO_BY_URL);){
            st.setString(1, url);
            ResultSet rs = st.executeQuery();
            if (rs.next()){
                String singer = rs.getString(SINGER);
                String name = rs.getString(NAME);
                Genre genre = Genre.valueOf(rs.getString(GENRE).toUpperCase());
                Float price = rs.getFloat(PRICE);
                Long id = rs.getLong(AUDIO_ID);
                audio = new Audio(id, singer, name, url, price, genre);
            }
        } catch (SQLException e) {
            throw new DAOException("SQLException in AudioDAO layer", e);
        }
        return audio;
    }

    /**
     * Method takeGenreIdByGenre
     *
     * @param genre of type Genre
     * @return Long
     * @throws DAOException
     */
    @Override
    public Long takeGenreIdByGenre(Genre genre) throws DAOException {
        Long id = null;
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
        PreparedStatement st = cn.prepareStatement(SQL_SELECT_GENRE_ID_BY_GENRE);){
            st.setString(1, GenreUtil.getGenre(genre));
            ResultSet rs = st.executeQuery();
            if (rs.next()){
                id = rs.getLong(GENRE_ID);
            }
        } catch (SQLException e) {
            throw new DAOException("SQLException in AudioDAO layer", e);
        }
        return id;
    }

    /**
     * Method editAudioInfo
     *
     * @param audio of type Audio
     * @return boolean
     * @throws DAOException
     */
    @Override
    public boolean editAudioInfo(Audio audio) throws DAOException {
        boolean flag = false;
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
        PreparedStatement st = cn.prepareStatement(SQL_UPDATE_AUDIO_INFO_BY_ID)){
            st.setString(1, audio.getSinger());
            st.setString(2, audio.getName());
            st.setFloat(3, audio.getPrice());
            st.setLong(4, audio.getAudioId());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            throw new DAOException("SQLException in AudioDAO layer(editAudioInfo)", e);
        }
        return flag;
    }

    /**
     * Method addGenreToNewAudio
     *
     * @param genreId of type Long
     * @param audioId of type Long
     * @return boolean
     * @throws DAOException
     */
    @Override
    public boolean addGenreToNewAudio(Long genreId, Long audioId) throws DAOException {
        boolean flag = false;
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
        PreparedStatement st = cn.prepareStatement(SQL_INSERT_GENRE_TO_NEW_AUDIO)){
            st.setLong(1, genreId);
            st.setLong(2, audioId);
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            throw new DAOException("SQLException", e);
        }
        return flag;
    }

    /**
     * Method isExist
     *
     * @param singer of type String
     * @param name   of type String
     * @return boolean
     * @throws DAOException
     */
    @Override
    public boolean isExist(String singer, String name) throws DAOException {
        boolean flag = false;
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
        PreparedStatement st = cn.prepareStatement(SQL_SELECT_AUDIO_BY_SINGER_AND_AUDIO_NAME)){
            st.setString(1, singer);
            st.setString(2, name);
            ResultSet rs = st.executeQuery();
            if (rs.next()){
                flag = true;
            }
        } catch (SQLException e) {
            throw new DAOException("SQLException", e);
        }
        return flag;
    }

    /**
     * Method takeAudioByAudioId
     *
     * @param id of type Long
     * @return Audio
     * @throws DAOException
     */
    @Override
    public Audio takeAudioByAudioId(Long id) throws DAOException {
        Audio audio = null;
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
        PreparedStatement st = cn.prepareStatement(SQL_SELECT_AUDIO_BY_AUDIO_ID)){
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()){
                String singer = rs.getString(SINGER);
                String name = rs.getString(NAME);
                Genre genre = GenreUtil.getGenreFromString(rs.getString(GENRE));
                Float price = rs.getFloat(PRICE);
                String url = rs.getString(URL);
                audio = new Audio(id, singer, name, url, price, genre);
            }
        } catch (SQLException e) {
            throw new DAOException("SQLException", e);
        }
        return audio;
    }
}

